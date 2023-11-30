package sbs.apirestclient.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jdk.jfr.Name;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotEmpty(message = "Cannot be an empty field")
    @Size(min=2, max= 15, message = "Size must be between 2 to 15 characters")
    private String name;

    @Column(nullable = false)
    @NotEmpty(message = "Cannot be an empty field")
    @Size(min=2, max= 25, message = "Size must be between 2 to 25 characters")
    private String lastname;

    @Column(unique = true, nullable = false)
    @NotEmpty(message = "Cannot be an empty field")
    @Email(message = "Invalid email address")
    private String email;

    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt;

    private String picture;

    /**
     * Is not necessary to create the opposite @OneToMany in Region entity
     */
    @ManyToOne
    @JoinColumn(name = "region_id")
    @NotNull(message = "Regions must be not null")
    private Region region;

    /**
     * @ PrePersist: It calls the function before the transaction
     */
    @PrePersist
    public void prePresist(){
        createAt = new Date();
    }
}
