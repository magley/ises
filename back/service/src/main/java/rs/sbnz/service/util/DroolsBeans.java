package rs.sbnz.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import rs.sbnz.service.ServiceApplication;

@Configuration
public class DroolsBeans {
    @Value("classpath:rules/**/*.drl")
    private Resource[] resources;
 
    @Bean
    public KieContainer kieContainer() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("rs.sbnz", "kjar", "0.0.1-SNAPSHOT"));
        KieScanner kScanner = ks.newKieScanner(kContainer);
        kScanner.start(1000);
        return kContainer;
    }

    // @Bean
    // public KieSession getKieSession() {
    //     KieServices ks = KieServices.Factory.get();
    //     KieContainer kContainer = ks.getKieClasspathContainer(); 
    //     KieSession ksession = kContainer.newKieSession("ksessionRealtimeClock");
    //     return ksession;
    // }

    @Bean
    public KieSession getKieSessionWithCreatedTemplates() throws IOException {
        String drl = "";
        drl += buildNormalRules() + "\n";
        drl += buildTemplateForReports() + "\n";
        drl += buildTemplateForSecondBatchOfTemplates() + "\n";
        drl = preCompile(drl);

        System.out.println("Creating KieSession...");
        KieSession session = createKieSessionFromDRL(drl);
        System.out.println("Creating KieSession... DONE");
        return session;
    }

    private String buildNormalRules() throws IOException {
        String drl = "";
        for (final Resource res : resources) {
            System.out.println(res.getFilename());

            InputStream ordinaryRules = res.getInputStream();
            byte[] ordinaryRulesBytes = IOUtils.toByteArray(ordinaryRules);
            drl += new String(ordinaryRulesBytes, StandardCharsets.UTF_8) + "\n";
        }
        return drl;
    }

    private String buildTemplateForReports() {
        InputStream template = ServiceApplication.class.getResourceAsStream("/reports/reportRules.drl");
        DataProvider dataProvider = new ArrayDataProvider(new String[][]{
            // Data to fill out template
            new String[]{"AttackType.AUTHENTICATION", "3"}
        });
        DataProviderCompiler converter = new DataProviderCompiler();
        return converter.compile(dataProvider, template);
    }

    private String buildTemplateForSecondBatchOfTemplates() {
        String drl = "";

        {
            InputStream template = ServiceApplication.class.getResourceAsStream("/templateRulesTwo/dosTemplate.drl");
            InputStream dosData = ServiceApplication.class.getResourceAsStream("/templateRulesTwo/dos_and_ddos_parameters.xls");
            ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
            drl += converter.compile(dosData, template, 3, 6);
        }

        {
            InputStream template = ServiceApplication.class.getResourceAsStream("/templateRulesTwo/ddosTemplate.drl");
            InputStream ddosData = ServiceApplication.class.getResourceAsStream("/templateRulesTwo/dos_and_ddos_parameters.xls");
            ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
            drl += converter.compile(ddosData, template, 3, 2);
        }

        return drl;
    }

    private String preCompile(String drl) {
        // Remove "package ..." from all the files. A single .drl can only
        // belong to one package, which we set to 'rules'. We could probably add
        // each .drl file manually with kieHelper.addContent(...), but this
        // works (for now).
        // NOTE: The package should probably not be "rules", because of the warning
        // output when compiling the rules:
        // File 'file0.drl' is in folder '' but declares package 'rules'. It is advised to have a correspondance between package and folder names.
        drl = drl.replaceAll("(?m)^package.*", "");
        drl = "package rules;\n" + drl;
        return drl;
    }

    private KieSession createKieSessionFromDRL(String drl){
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.printf("[KJAR_BUILDER::%s]: %s\n", message.getLevel().toString(), message.getText());
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs for KJAR_BUILDER");
        }

        KieBaseConfiguration kbaseConf = KieServices.Factory.get().newKieBaseConfiguration();
        kbaseConf.setOption(EventProcessingOption.STREAM);

        return kieHelper.build(kbaseConf).newKieSession();
    }
}
