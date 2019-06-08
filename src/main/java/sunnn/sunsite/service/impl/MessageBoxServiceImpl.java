package sunnn.sunsite.service.impl;

import org.springframework.stereotype.Service;
import sunnn.sunsite.dao.SysDao;
import sunnn.sunsite.service.MessageBoxService;
import sunnn.sunsite.util.SunsiteConstant;

import javax.annotation.Resource;

@Service
public class MessageBoxServiceImpl implements MessageBoxService {

    @Resource
    private SysDao sysDao;

    @Override
    public String getMessage() {
        String msgBox = sysDao.selectMsg();
        String[] messages = msgBox.split(SunsiteConstant.MSG_BOX_SEPARATOR);

        if (messages.length == 0 ||
                (messages.length == 1 && messages[0].isEmpty())) {
            sysDao.updateMsgBox(sysDao.selectVersion(), "");
            return null;
        }

        StringBuilder updateMsg = new StringBuilder();
        String res = null;
        for (String message : messages) {
            if (message.isEmpty())
                continue;

            if (res == null) {
                res = message;
                continue;
            }

            if (updateMsg.length() != 0)
                updateMsg.append(SunsiteConstant.MSG_BOX_SEPARATOR);
            updateMsg.append(message);
        }

        sysDao.updateMsgBox(sysDao.selectVersion(), updateMsg.toString());
        return res;
    }

    @Override
    public void pushMessage(String msg) {
        if (msg == null || msg.isEmpty())
            return;

        String msgBox = sysDao.selectMsg();
        String[] messages = msgBox.split(SunsiteConstant.MSG_BOX_SEPARATOR);

        StringBuilder updateMsg = new StringBuilder();
        for (String message : messages) {
            if (message.isEmpty())
                continue;

            updateMsg.append(message)
                    .append(SunsiteConstant.MSG_BOX_SEPARATOR);
        }

        updateMsg.append(msg);
        sysDao.updateMsgBox(sysDao.selectVersion(), updateMsg.toString());
    }
}
