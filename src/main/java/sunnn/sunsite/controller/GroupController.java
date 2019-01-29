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
import sunnn.sunsite.dto.response.BaseResponse;
import sunnn.sunsite.dto.response.CollectionListResponse;
import sunnn.sunsite.dto.response.GroupListResponse;
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
    public GroupListResponse groupList(@RequestParam("p") int page) {
        return groupService.getGroupList(page);
    }

    @GetMapping(value = "/info")
    @ResponseBody
    public CollectionListResponse groupDetail(@RequestParam("n") String group) {
        return collectionService.getCollectionListByGroup(group);
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
    public BaseResponse modifyGroup(@Valid @RequestBody ModifyGroup modifyInfo) {
        StatusCode modifyAlias = groupService.modifyAlias(
                modifyInfo.getGroup(), modifyInfo.getAliases());
        StatusCode modifyName = groupService.modifyName(modifyInfo.getGroup(), modifyInfo.getNewName());

        if (!modifyAlias.equals(StatusCode.OJBK))
            return new BaseResponse(modifyAlias);
        else if (!modifyName.equals(StatusCode.OJBK))
            return new BaseResponse(modifyName);
        else
            return new BaseResponse(StatusCode.OJBK);
    }

    @PostMapping(value = "/delete")
    @ResponseBody
    public BaseResponse deleteGroup(@Valid @RequestBody DeleteRequest info) {
        return new BaseResponse(
                groupService.delete(info.getDeleteInfo()));
    }
}
