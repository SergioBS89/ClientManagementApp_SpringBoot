package sbs.apirestclient.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sbs.apirestclient.entities.Client;
import sbs.apirestclient.entities.Region;

import java.util.List;

public interface IClient extends JpaRepository<Client,Integer> {

    /**
     * Function to create pagination
     */
    public Page<Client> findAll(Pageable pageable);

    @Query("from Region")
    public List<Region> findAllRegions();
}
