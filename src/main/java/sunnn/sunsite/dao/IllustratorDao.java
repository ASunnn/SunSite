package sunnn.sunsite.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import sunnn.sunsite.entity.Illustrator;

import java.util.List;

@Repository
public class IllustratorDao extends MongoBase<Illustrator> {

    public Illustrator findOne(String name) {
        return findOne(
                Query.query(Criteria.where("name").is(name)),
                Illustrator.class);
    }

    public List<Illustrator> getAllIllustrator() {
        return findAll(Illustrator.class);
    }

    public boolean delete(String name) {
        return remove(
                Query.query(Criteria.where("name").is(name)),
                Illustrator.class);
    }

    public boolean updateIllustrator(String oldName, String newName) {
        return updateOne(
                Query.query(Criteria.where("name").is(oldName)),
                Update.update("name", newName),
                Illustrator.class);
    }

}
