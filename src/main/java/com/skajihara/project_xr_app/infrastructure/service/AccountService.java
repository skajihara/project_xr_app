package com.skajihara.project_xr_app.infrastructure.service;

import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import com.skajihara.project_xr_app.domain.repository.AccountRepository;
import com.skajihara.project_xr_app.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * 全てのアカウント情報を取得する
     *
     * @return 全アカウント情報
     */
    public List<AccountRecord> getAllAccounts() {
        return accountRepository.selectAllAccounts();
    }

    /**
     * 全てのアカウント情報を取得する
     *
     * @param id アカウントID
     * @return 全アカウント情報
     */
    public AccountRecord getAccount(String id) throws NotFoundException {
        AccountRecord account = accountRepository.selectAccount(id);
        if (account == null) {
            throw new NotFoundException("Account id: " + id + " is not found");
        }
        return account;
    }
}
