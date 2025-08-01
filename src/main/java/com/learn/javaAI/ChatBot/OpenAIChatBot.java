package com.learn.javaAI.ChatBot;

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

public class OpenAIChatBot {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String api_key = dotenv.get("OPENAI_API_KEY");
    public static void main(String []args){
        Scanner sc = new Scanner(System.in);

        System.out.println("Chatbot Initialized. Type 'exit' to stop.");

        while (true){
            System.out.println("You: ");
            String userInput = sc.nextLine();
            if(userInput.equals("exit")){
                break;
            }
            String response = getChatBotResponse(userInput);
        }
        sc.close();
    }

    private static String getChatBotResponse(String userInput) {
        String url = "https://api.openai.com/v1/completions";

        //Create a json payload for OpenAI
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("model", "gpt-3.5-turbo");
        jsonRequest.addProperty("prompt", userInput);
        jsonRequest.addProperty("max_tokens",100);

        try {
            //Send request and return the response
            HttpClient client = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(url);

            //set Authorization
            postRequest.setHeader("Authorization", "Bearer " + api_key);
            postRequest.setHeader("Content-Type", "application/json");

            //Add json request to the body
            postRequest.setEntity(new StringEntity(jsonRequest.toString()));

            //Get response
            HttpResponse response = client.execute(postRequest);
            String responseBody = EntityUtils.toString(response.getEntity());

            //Parse the json response
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonResponse.getAsJsonArray().get(0)
                    .getAsJsonObject()
                    .get("text")
                    .getAsString()
                    .trim();
        } catch (IOException e){
            e.printStackTrace();
            return "Sorry, I couldn't get a response from the chatbot.";
        }
    }
}
