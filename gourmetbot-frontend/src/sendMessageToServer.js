import axios from 'axios';

const baseUrl = "http://localhost:8090/api/v1";

export const sendMessagesToServer = async (message, conversationId) => {
    const response =
        await axios.post(`${baseUrl}/chat`
            , {message}
            ,{headers: {"Conversation-Id" : conversationId}});

    return response.data;
}
