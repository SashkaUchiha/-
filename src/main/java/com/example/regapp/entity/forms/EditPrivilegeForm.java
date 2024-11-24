package com.example.regapp.entity.forms;

import com.example.regapp.entity.Permission;
import lombok.Data;

@Data
public class EditPrivilegeForm {
    private String userIdentifier;
    private String fileName;
    private Permission permission;
}
