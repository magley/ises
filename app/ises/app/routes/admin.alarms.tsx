import {
    useStompClient,
    useSubscription,
} from "react-stomp-hooks";

export default function Alarms() {
    useSubscription("/topic/alarms", (message) => {
        console.log(message);
    });

    const stompClient = useStompClient();

    const publishMessage = () => {
        if (stompClient) {
            console.log("AAA");
            stompClient.publish({ destination: '/chat', })
        }
    }
    return (
        <div onClick={publishMessage}> Send message </div>
    )
}