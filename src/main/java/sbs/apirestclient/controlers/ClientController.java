package sbs.apirestclient.controlers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sbs.apirestclient.entities.Client;
import sbs.apirestclient.entities.Region;
import sbs.apirestclient.entities.ResponseAndErrorHandler;
import sbs.apirestclient.entities.TypeTransaction;
import sbs.apirestclient.service.ClientService;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @CrossOrigin It allows to share the api to the next url
 */
@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;

    private final Logger log = LoggerFactory.getLogger(ClientController.class);

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<Client> getClients() {
        return clientService.findAll();
    }

    @GetMapping("/pages/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Page<Client> getClients(@PathVariable Integer page) {
        return clientService.findAllPagination(PageRequest.of(page, 8));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Integer id) {
        return new ResponseAndErrorHandler().sendResponse(TypeTransaction.GET, null, id, clientService, null);
    }


    /**
     * @param bindingResult This parameter is used to work with validations, is important to put it after @RequestBody and before @PathVariable
     * @return
     * @Valid This annotation allows to use the validations from entity class, should be as a first parameter
     */
    @PostMapping("")
    public ResponseEntity<?> createClient(@Valid @RequestBody Client client, BindingResult bindingResult) {

        return new ResponseAndErrorHandler().sendResponse(TypeTransaction.POST, client, null, clientService, bindingResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
        return new ResponseAndErrorHandler().sendResponse(TypeTransaction.DELETE, null, id, clientService, null);
    }

    /**
     * @param bindingResult This parameter is use to work with validations too, is important to put it after @RequestBody and before @PathVariable
     * @return
     * @Valid This annotation allows use the validations from entity class, should as a first parameter
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@Valid @RequestBody Client client, BindingResult bindingResult, @PathVariable Integer id) {
        return new ResponseAndErrorHandler().sendResponse(TypeTransaction.PUT, client, id, clientService, bindingResult);
    }

    /**
     * This function is to upload the icon of every user
     * @param file
     * @param id
     * @return
     */
    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer id) {
        Map<String, Object> response = new HashMap<>();
        Client client = clientService.findById(id);
        if (!file.isEmpty()) {
            //UUID allow generate a unique name to avoid duplicates
            String fileName = UUID.randomUUID().toString().concat("_" + file.getOriginalFilename());
            Path routeFile = Paths.get("uploadFiles").resolve(fileName).toAbsolutePath();
            log.info(routeFile.toString());

            //Asking if exist a picture from the choose client
            String oldNamePicture = client.getPicture();
            if (oldNamePicture != null && oldNamePicture.length() > 0) {
                Path routeOldFile = Paths.get("uploadFiles").resolve(oldNamePicture).toAbsolutePath();
                File oldFile = routeOldFile.toFile();
                if (oldFile.exists() && oldFile.canRead()) {
                    oldFile.delete();
                }
            }

            try {
                Files.copy(file.getInputStream(), routeFile);
            } catch (IOException e) {
                e.printStackTrace();
                response.put("message", "Error uploading file");
                response.put("error", e.getMessage());
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            client.setPicture(fileName);
            clientService.save(client);
            response.put("client", client);
            response.put("message", fileName + " has been upload correctly");
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @GetMapping("/files/img/{filename:.+}")
    public ResponseEntity<?> getFile(@PathVariable String filename) {

        Path routeFile = Paths.get("uploadFiles").resolve(filename).toAbsolutePath();
        Resource resource = null;
        log.info(routeFile.toString());

        try {
            resource = new UrlResource(routeFile.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (!resource.exists() && !resource.isReadable()) {
            //It assigns the picture by default to each user
            routeFile = Paths.get("uploadFiles/default/user.png").toAbsolutePath();
            try {
                resource = new UrlResource(routeFile.toUri());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            log.info("Next file not exist or is not readable: ".concat(filename).concat(", this user will have assigned a picture by default "));
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

        return new ResponseEntity<Resource>(resource, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/regions")
    public List<Region> getAllRegions(){
        return clientService.findAllRegions();
    }
}
