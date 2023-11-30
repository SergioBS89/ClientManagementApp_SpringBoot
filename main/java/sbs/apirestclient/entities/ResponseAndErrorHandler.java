package sbs.apirestclient.entities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import sbs.apirestclient.service.ClientService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ResponseAndErrorHandler {

    public ResponseAndErrorHandler() {
    }

    public ResponseEntity<?> sendResponse(TypeTransaction typeTransaction, Client client, Integer id, ClientService clientService, BindingResult bindingResult) {

        Client clientTransactional;
        Map<String, Object> response = new HashMap<>();
        ResponseEntity responseEntity = null;
        List<String> errors;

        switch (typeTransaction) {
            case POST:

                //Validations from entity annotations
                if (bindingResult.hasErrors()) {
                    errors = bindingResult.getFieldErrors()
                            .stream()
                            .map(mapper -> "Field " + mapper.getField().concat(": " + mapper.getDefaultMessage()))
                            .collect(Collectors.toList());
                    response.put("errors", errors);
                }
                //Managing errors in transactions with DB
                try {
                    clientTransactional = clientService.save(client);
                    response.put("message", "Client has been created successfully");
                    response.put("client", clientTransactional);
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
                } catch (Exception e) {
                    response.put("message", "Error creating a new client");
                    response.put("error", e.getMessage());
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                }
                break;

            case GET:

                //Managing error transactions with DB
                try {
                    client = clientService.findById(id);
                    responseEntity = new ResponseEntity<Client>(client, HttpStatus.OK);
                } catch (NoSuchElementException e) {
                    response.put("message", "Client with ID ".concat(id.toString().concat(" no exist")));
                    response.put("error", e.getMessage());
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                    response.put("message: ", "Transaction error ");
                    response.put("error", e.getMessage());
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;

            case PUT:

                //Validations from entity annotations
                if (bindingResult.hasErrors()) {
                    errors = bindingResult.getFieldErrors()
                            .stream()
                            .map(mapper -> "Field " + mapper.getField().concat(": " + mapper.getDefaultMessage()))
                            .collect(Collectors.toList());

                    response.put("errors", errors);
                }
                //Managing error transactions with DB
                try {
                    Client currentClient = clientService.findById(id);
                    currentClient.setName(client.getName());
                    currentClient.setLastname(client.getLastname());
                    currentClient.setEmail(client.getEmail());
                    currentClient.setCreateAt(new Date());
                    currentClient.setRegion(client.getRegion());
                    clientTransactional = clientService.save(currentClient);
                    response.put("message", "Client sucessfully updated");
                    response.put("client", clientTransactional);
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
                } catch (Exception e) {
                    response.put("message", "Error updating client");
                    response.put("error", e.getMessage());
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;

            case DELETE:

                //Managing error transactions with DB
                try {
                    Client validateClient = clientService.findById(id);
                    //Asking if exist a picture from the choose client to remove picture if exist
                    String oldNamePicture = validateClient.getPicture();
                    if (oldNamePicture != null && oldNamePicture.length() > 0) {
                        Path routeOldFile = Paths.get("uploadFiles").resolve(oldNamePicture).toAbsolutePath();
                        File oldFile = routeOldFile.toFile();
                        if (oldFile.exists() && oldFile.canRead()) {
                            oldFile.delete();
                        }
                    }
                    clientService.delete(id);
                    response.put("message", "Client successfully deleted");
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
                } catch (Exception e) {
                    response.put("message", "Error deleting client with ID ".concat(id.toString()));
                    response.put("error", e.getMessage());
                    responseEntity = new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
        }
        return responseEntity;
    }
}
