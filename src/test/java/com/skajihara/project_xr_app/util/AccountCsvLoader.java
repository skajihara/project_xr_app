package com.skajihara.project_xr_app.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.skajihara.project_xr_app.domain.entity.record.AccountRecord;
import com.skajihara.project_xr_app.domain.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class AccountCsvLoader {

    @Autowired
    private AccountRepository accountRepository;

    public void loadTestAccounts(String filePath) {

        accountRepository.deleteAll();
        if (filePath.equals("")) {
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                AccountRecord account = new AccountRecord();
                account.setId(line[0]);
                account.setName(line[1]);
                account.setBio(line[2]);
                account.setIcon(line[3]);
                account.setHeaderPhoto(line[4]);
                account.setLocation(line[5]);
                account.setBirthday(LocalDate.parse(line[6]));
                account.setRegistered(LocalDate.parse(line[7]));
                account.setFollowing(Integer.valueOf(line[8]));
                account.setFollower(Integer.valueOf(line[9]));
                account.setValidFlag(Integer.valueOf(line[10]));
                account.setDeleteFlag(Integer.valueOf(line[11]));
                accountRepository.save(account);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
