package org.oporaua.localelections.violations.model;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class ViolationParent implements ParentListItem {

    private String name;
    private List<ViolationChild> childList;

    public ViolationParent(String name, List<ViolationChild> childList) {
        this.name = name;
        this.childList = childList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<ViolationChild> getChildItemList() {
        return childList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

}
