package maeng0830.hellospring.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // jpa가 관리하는 entity다.
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // identity는 DB가 알아서 값을 생성해주는 것
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
