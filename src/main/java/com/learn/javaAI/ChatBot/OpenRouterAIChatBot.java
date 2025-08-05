package com.learn.javaAI.ChatBot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.Scanner;

public class OpenRouterAIChatBot {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String api_key = dotenv.get("OPENAI_API_KEY");
    public static void main(String []args){
        Scanner sc = new Scanner(System.in);

        System.out.println("\uD83E\uDD16 Chatbot Initialized (OpenRouter). Type 'exit' to stop.");

        while (true){
            System.out.println("You: ");
            String userInput = sc.nextLine();
            if(userInput.equals("exit")){
                System.out.println("👋 Goodbye!");
                break;
            }
            String response = getChatBotResponse(userInput);
            System.out.println("Bot: " + response);
        }
        sc.close();
    }

    private static String getChatBotResponse(String userInput) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        //Create a json payload for OpenAI
        JsonArray jsonRequestArr = new JsonArray ();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", userInput);
        jsonRequestArr.add(message);

        // System instruction for general concise behavior
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content",
                "You are a helpful AI assistant. Respond in a clear and concise manner. Do not include unnecessary explanations unless the user asks for them. If the user asks for one-word or brief responses, return only that.");
        jsonRequestArr.add(systemMessage);
        JsonObject payload = new JsonObject();
        payload.addProperty("model", "mistralai/mistral-7b-instruct");
        payload.addProperty("max_tokens",100);
        payload.addProperty("temperature",0.7);
        payload.add("messages",jsonRequestArr);
        try {
            //Send request and return the response
            HttpClient client = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(url);

            //set Authorization
            postRequest.setHeader("Authorization", "Bearer " + api_key);
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setHeader("X-Title", "Java Chatbot");

            //Add json request to the body
            postRequest.setEntity(new StringEntity(payload.toString()));

            //Get response
            HttpResponse response = client.execute(postRequest);
            String responseBody = EntityUtils.toString(response.getEntity());

            //Parse the json response
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonResponse.get("choices")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString()
                    .trim();
        } catch (IOException e){
            e.printStackTrace();
            return "Sorry, I couldn't get a response from the chatbot.";
        }
    }
}
