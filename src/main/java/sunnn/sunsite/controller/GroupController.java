package sunnn.sunsite.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sunnn.sunsite.dto.request.DeleteRequest;
import sunnn.sunsite.dto.request.ModifyGroup;
import sunnn.sunsite.dto.response.*;
import sunnn.sunsite.exception.IllegalFileRequestException;
import sunnn.sunsite.service.CollectionService;
import sunnn.sunsite.service.GroupService;
import sunnn.sunsite.util.StatusCode;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    private final CollectionService collectionService;

    @Autowired
    public GroupController(GroupService groupService, CollectionService collectionService) {
        this.groupService = groupService;
        this.collectionService = collectionService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public GroupListResponse groupList(@RequestParam("p") int page, @RequestParam(value = "query", required = false) String query) {
        if (query == null || query.isEmpty())
            return groupService.getGroupList(page);
        else
            return groupService.getGroupList(query.trim(), page);
    }

    @GetMapping(value = "/info")
    @ResponseBody
    public GroupInfoResponse groupInfo(@RequestParam("n") String group) {
        return groupService.getGroupInfo(group);
    }

    @GetMapping(value = "/detail")
    @ResponseBody
    public CollectionListResponse groupDetail(@RequestParam("n") String group) {
        return collectionService.getCollectionListByGroup(group);
    }

    @GetMapping(value = "/m/{name}")
    @ResponseBody
    public ResponseEntity groupThumbnail(@PathVariable("name") String name) throws IOException {
        File file = groupService.getGroupThumbnail(name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/download/{name}")
    @ResponseBody
    public ResponseEntity downloadGroup(@PathVariable("name") String name) throws IllegalFileRequestException, IOException {
        File file = groupService.download(name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                new String(file.getName().getBytes(), "ISO-8859-1"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/modify")
    @ResponseBody
    public ModifyResultResponse modifyGroup(@Valid @RequestBody ModifyGroup modifyInfo) {
        StatusCode modifyAlias = groupService.modifyAlias(
                modifyInfo.getGroup(), modifyInfo.getAliases());
        StatusCode modifyName = groupService.modifyName(modifyInfo.getGroup(), modifyInfo.getNewName());

        String newLink = modifyName.equals(StatusCode.OJBK) ? modifyInfo.getNewName() : modifyInfo.getGroup();
        ModifyResultResponse response;
        if (!modifyAlias.equals(StatusCode.OJBK))
            response = new ModifyResultResponse(modifyAlias);
        else if (!modifyName.equals(StatusCode.OJBK))
            response = new ModifyResultResponse(modifyName);
        else
            response = new ModifyResultResponse(StatusCode.OJBK);

        return response.setNewLinkInfo(newLink);
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteGroup(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                groupService.delete(info.getDeleteInfo()));
    }
}
