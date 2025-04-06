package com.skajihara.project_xr_app.infrastructure.controller;

import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import com.skajihara.project_xr_app.exception.NotFoundException;
import com.skajihara.project_xr_app.infrastructure.controller.model.AccountModel;
import com.skajihara.project_xr_app.infrastructure.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * 全てのアカウント情報を取得する
     *
     * @return 全アカウント情報
     */
    @GetMapping
    public List<AccountModel> getAllAccounts() {
        return convertToResponseModelList(accountService.getAllAccounts());
    }

    /**
     * 1件のアカウント情報を取得する
     *
     * @param id アカウントID
     * @return 全アカウント情報
     */
    @GetMapping("/{id}")
    public AccountModel getAccount(@PathVariable String id) throws NotFoundException {
        return convertToResponseModel(accountService.getAccount(id));
    }

    private AccountModel convertToResponseModel(AccountRecord serviceResult) {
        if (serviceResult == null) {
            return new AccountModel();
        }
        AccountModel responseResult = new AccountModel();
        responseResult.setId(serviceResult.getId());
        responseResult.setName(serviceResult.getName());
        responseResult.setBio(serviceResult.getBio());
        responseResult.setIcon(serviceResult.getIcon());
        responseResult.setHeaderPhoto(serviceResult.getHeaderPhoto());
        responseResult.setLocation(serviceResult.getLocation());
        responseResult.setBirthday(serviceResult.getBirthday());
        responseResult.setRegistered(serviceResult.getRegistered());
        responseResult.setFollowing(serviceResult.getFollowing());
        responseResult.setFollower(serviceResult.getFollower());
        responseResult.setValidFlag(serviceResult.getValidFlag());
        responseResult.setDeleteFlag(serviceResult.getDeleteFlag());
        return responseResult;
    }

    private List<AccountModel> convertToResponseModelList(List<AccountRecord> serviceResults) {
        return serviceResults.stream()
                .map(this::convertToResponseModel)
                .collect(Collectors.toList());
    }
}
