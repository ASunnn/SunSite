package sunnn.sunsite.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sunnn.sunsite.dao.AliasDao;
import sunnn.sunsite.entity.Alias;
import sunnn.sunsite.service.AliasService;
import sunnn.sunsite.util.StatusCode;

import javax.annotation.Resource;
import java.util.*;

@Service
public class AliasServiceImpl implements AliasService {

    @Resource
    private AliasDao aliasDao;

    @Override
    public String[] getAlias(int origin, int kind) {
        List<String> aliasList = aliasDao.getAllAliasByOrigin(origin, kind);

        if (aliasList.isEmpty())
            return new String[0];
        else {
            String[] aliases = new String[aliasList.size()];
            aliasList.toArray(aliases);
            return aliases;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.DEFAULT)
    public StatusCode modifyAlias(int origin, int kind, String[] aliases) {
        String[] aliasRecord = getAlias(origin, kind);

        HashSet<String> aliasSet = new HashSet<>();
        for (int i = 0; i < aliases.length; ++i)
            aliases[i] = aliases[i].trim();
        Collections.addAll(aliasSet, aliases);

        for (String a : aliasRecord) {
            if (aliasSet.contains(a)) {
                aliasSet.remove(a);
            } else {
                deleteAlias(new Alias()
                        .setOrigin(origin)
                        .setKind(kind)
                        .setAlias(a));
            }
        }

        List<Alias> aliasList = new ArrayList<>();
        for (String a : aliasSet) {
            if (!a.isEmpty()) {
                aliasList.add(new Alias()
                        .setAlias(a)
                        .setOrigin(origin)
                        .setKind(kind));
            }
        }
        if (!aliasList.isEmpty())
            aliasDao.insertAllAlias(aliasList);
        return StatusCode.OJBK;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT)
    public StatusCode deleteAlias(Alias alias) {
        aliasDao.deleteAlias(alias);
        return StatusCode.OJBK;
    }
}
