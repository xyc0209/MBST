package com.mbs.common.base;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class MDemandChain {

    private List<String> demandIdList;          // This stands for a intact demand chains
    private Map<String, MUserDemand> demandMap; // Store the demands

    public MDemandChain() {
        this.demandIdList = new ArrayList<>();
        this.demandMap = new HashMap<>();
    }

    public boolean verify() {
        for (String demandId : demandMap.keySet()) {
            //判断用户需求是否是
            if (!demandMap.get(demandId).getId().equals(demandId)) {
                return false;
            }
        }
        return true;
    }

    public void addDemand(MUserDemand userDemand) {
        this.demandMap.put(userDemand.getId(), userDemand);
        this.demandIdList.add(userDemand.getId());
    }

    public void addDemandList(List<MUserDemand> demandList) {
        for (MUserDemand userDemand : demandList) {
            this.addDemand(userDemand);
        }
    }

    public List<MUserDemand> getDemandList() {
        List<MUserDemand> demandList = new ArrayList<>();
        for (String demandId : demandIdList) {
            demandList.add(this.demandMap.get(demandId));
        }
        return demandList;
    }

    public boolean containsDemandId(String demandId) {
        return this.demandMap.containsKey(demandId);
    }

    public MUserDemand getDemandById(String demandId) {
        return this.demandMap.getOrDefault(demandId, null);
    }

    @Override
    public String toString() {
        return "MDemandChain{" +
                "demandIdList=" + demandIdList +
                ", demandMap=" + demandMap +
                '}';
    }

    public int getDemandIndex(String demandId) {
        for (int i = 0; i < this.demandIdList.size(); ++i) {
            if (this.demandIdList.get(i).equals(demandId)) {
                return i;
            }
        }
        return -1;
    }
}
