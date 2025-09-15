package webapp;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

interface NameCountryCount {
    String getName();
    String getCountry();
    long getCount();
}

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("""
    select p.name as name, p.country as country, count(p) as count
    from Person p
    group by p.name, p.country
    order by p.name, p.country
  """)
    List<NameCountryCount> aggregate();
}
