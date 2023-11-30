package sbs.apirestclient.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sbs.apirestclient.entities.Client;
import sbs.apirestclient.entities.Region;
import sbs.apirestclient.interfaces.IClient;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    private IClient iClient;

    public List<Client> findAll(){
        return iClient.findAll();
    }

    @Transactional
    public Page<Client> findAllPagination(Pageable pageable){
        return iClient.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Client findById(Integer id){
        return iClient.findById(id).get();
    }

    @Transactional
    public Client save(Client client){
        return iClient.save(client);
    }

    @Transactional
    public void delete(Integer id){
        iClient.deleteById(id);
    }

    @Transactional
    public  List<Region> findAllRegions(){
        return iClient.findAllRegions();
    }

}
