package com.shoppinglist.shoppinglist.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shoppinglist.shoppinglist.ai.dto.receipt.OcrResult;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.CreateAIShoppingListItemDto;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AIService {
    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object generateShoppingListFromUrl(String url) {
        // Verify if the input is a valid URL
        if (!isValidUrl(url)) {
            return new IllegalArgumentException("Provided input is not a valid URL.");
        }

        String pageContent = "pęczek zielonych szparagów\n" +
                "4jajka\n" +
                "8młodych ziemniaków\n" +
                "10plastrów szynki parmeńskiej\n" +
                "1natka koperku\n" +
                "1łyżka masła\n" +
                "oliwa z oliwek";
        String prompt = "Generate a shopping list (JSON -> name, quantity) from the ingredients: " + pageContent;

        JsonNode requestBody = buildRequestBodyShoppingItems(prompt);
        return callChatGPT(requestBody);
    }

    private String callChatGPT(JsonNode requestBody) {
        try {
            // Serializacja JSON do String
            String requestBodyString = objectMapper.writeValueAsString(requestBody);

            // Przygotowanie nagłówków zapytania
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.set("Content-Type", "application/json");

            // Stworzenie encji HTTP z ciałem zapytania i nagłówkami
            HttpEntity<String> entity = new HttpEntity<>(requestBodyString, headers);

            // Wykonanie zapytania za pomocą RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(openAiApiUrl, entity, String.class);

            // Sprawdzenie, czy odpowiedź jest poprawna
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parsowanie odpowiedzi do obiektu JSON
                JsonNode responseBody = objectMapper.readTree(response.getBody());

                // Wyciąganie wartości `content` z `choices[0].message`
                JsonNode choicesArray = responseBody.get("choices");
                if (choicesArray != null && choicesArray.isArray() && choicesArray.size() > 0) {
                    JsonNode firstChoice = choicesArray.get(0);
                    JsonNode message = firstChoice.get("message");
                    if (message != null) {
                        JsonNode content = message.get("content");
                        if (content != null) {
                            JsonNode responseContent = objectMapper.readTree(content.asText());
                            // Pobieramy wartość pod kluczem "shoppingItems"
                            JsonNode shoppingItems = responseContent.get("shoppingItems");
                            if (content != null) {
                                // Zwracamy treść odpowiedzi asystenta jako String
                                return content.asText();
                            }
                        }
                    }
                }
            }

            throw new IllegalArgumentException("Unable to extract content from the response.");
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occurred while communicating with ChatGPT.", e);
        }
    }

    private boolean isValidUrl(String url) {
        String urlRegex = "^(https?|ftp)://[\\w.-]+(?:\\.[\\w.-]+)+[/\\w._-]*$";
        return Pattern.matches(urlRegex, url);
    }

    // Metoda budująca JSON z wykorzystaniem ObjectMapper
    private JsonNode buildRequestBodyShoppingItems(String content) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Tworzenie obiektu JSON dla wiadomości
            ObjectNode messageNode = mapper.createObjectNode()
                    .put("role", "user")
                    .put("content", content);

            // Tworzenie głównego obiektu JSON dla żądania
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("temperature", 1.0);
            requestBody.put("max_tokens", 1024);

            // Dodanie tablicy wiadomości
            requestBody.set("messages", mapper.createArrayNode().add(messageNode));

            // Dodanie schematu odpowiedzi
            requestBody.set("response_format", createResponseFormat());

            return requestBody;
        } catch (Exception e) {
            throw new RuntimeException("Błąd przy tworzeniu JSON request", e);
        }
    }

    // Metoda budująca format odpowiedzi dla listy zakupów
    private ObjectNode createResponseFormat() {
        try {
            ObjectNode responseFormatNode = objectMapper.createObjectNode();
            responseFormatNode.put("type", "json_schema");

            // Tworzenie schematu JSON dla odpowiedzi
            ObjectNode jsonSchema = objectMapper.createObjectNode();
            jsonSchema.put("name", "shopping_list_response");
            jsonSchema.put("strict", true);

            // Tworzenie schematu głównego
            ObjectNode schemaNode = objectMapper.createObjectNode();
            schemaNode.put("type", "object");

            // Definiowanie 'shoppingItems' jako tablicy
            ObjectNode shoppingItemsNode = objectMapper.createObjectNode();
            shoppingItemsNode.put("type", "array");
            shoppingItemsNode.put("description", "Generate a shopping list (JSON -> name, quantity) from the ingredients");

            // Definiowanie elementów tablicy 'items'
            ObjectNode itemsNode = objectMapper.createObjectNode();
            itemsNode.put("type", "object");

            // Dodanie właściwości do 'items'
            ObjectNode itemProperties = objectMapper.createObjectNode();
            ObjectNode nameNode = objectMapper.createObjectNode();
            nameNode.put("type", "string");
            nameNode.put("description", "Nazwa produktu");

            ObjectNode quantityNode = objectMapper.createObjectNode();
            quantityNode.put("type", "number");
            quantityNode.put("description", "Ilość");

            itemProperties.set("name", nameNode);
            itemProperties.set("quantity", quantityNode);

            itemsNode.set("properties", itemProperties);
            itemsNode.putArray("required").add("name").add("quantity");
            itemsNode.put("additionalProperties", false);

            shoppingItemsNode.set("items", itemsNode);

            // Dodanie 'shoppingItems' do schematu
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            propertiesNode.set("shoppingItems", shoppingItemsNode);

            schemaNode.set("properties", propertiesNode);
            schemaNode.putArray("required").add("shoppingItems");
            schemaNode.put("additionalProperties", false);

            jsonSchema.set("schema", schemaNode);

            responseFormatNode.set("json_schema", jsonSchema);

            return responseFormatNode;

        } catch (Exception e) {
            throw new RuntimeException("Błąd przy tworzeniu JSON dla odpowiedzi", e);
        }
    }

    public List<CreateAIShoppingListItemDto> generateShoppingListFromText(String text) {
        String examplePrompt = "#EXAMPLE: [{'name': 'miód 250g', 'quantity': 1}, {'name': 'płatki owsiane 500g', 'quantity': 1}, {'name': 'banany 3 sztuki', 'quantity': 1}, {'name': 'orzechy włoskie 100g', 'quantity': 1}]";
        String prompt = "Wygeneruj listę zakupów w formacie JSON zawierającą pola 'name' i 'quantity' na podstawie poniższych składników. Wszystkie dodatkowe informacje, takie jak ilości, jednostki miary czy szczegóły produktu, umieść w polu 'name'. Pole 'quantity' powinno zawierać tylko liczbę opakowań lub sztuk do kupienia. Nie używaj żadnych jednostek miary ani dodatkowych opisów w polu 'quantity'.\n\nSkładniki:\n" + text + examplePrompt;
        JsonNode requestBody = buildRequestBodyShoppingItems(prompt);

        String assistantResponse = callChatGPT(requestBody);
        if (assistantResponse != null) {
            try {
                // Parsowanie odpowiedzi asystenta
                JsonNode responseContent = objectMapper.readTree(assistantResponse);
                JsonNode shoppingItemsNode = responseContent.get("shoppingItems");
                if (shoppingItemsNode != null && shoppingItemsNode.isArray()) {
                    List<CreateAIShoppingListItemDto> shoppingList = new ArrayList<>();
                    for (JsonNode itemNode : shoppingItemsNode) {
                        String name = itemNode.get("name").asText();
                        int quantity = itemNode.get("quantity").asInt();
                        UUID id = UUID.randomUUID();
                        CreateAIShoppingListItemDto item = CreateAIShoppingListItemDto.builder().name(name).quantity(quantity).purchased(false).id(id).build();
                        shoppingList.add(item);
                    }
                    return shoppingList;
                } else {
                    throw new IllegalArgumentException("Odpowiedź nie zawiera poprawnej listy zakupów.");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Błąd podczas parsowania odpowiedzi asystenta.", e);
            }
        } else {
            throw new IllegalArgumentException("Otrzymano pustą odpowiedź od asystenta.");
        }
    }

    //    OcrResult
//    String
    public OcrResult analyzeReceiptImage(String imagePath) {
        String receiptOcrEndpoint = "https://ocr.asprise.com/api/v1/receipt";
        File imageFile = new File(imagePath);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(receiptOcrEndpoint);
            post.setEntity(MultipartEntityBuilder.create()
                    .addTextBody("api_key", "TEST")       // TEST mode
                    .addTextBody("recognizer", "auto")    // auto-detect region
                    .addTextBody("ref_no", "ocr_java_123")
                    .addPart("file", new FileBody(imageFile))
                    .build());

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
//                return jsonResponse; // Zwróć surowy JSON jako String

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(jsonResponse, OcrResult.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing receipt image: " + e.getMessage(), e);
        }
    }
}
