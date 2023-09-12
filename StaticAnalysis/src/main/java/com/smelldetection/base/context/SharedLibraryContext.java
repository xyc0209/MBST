package com.smelldetection.base.context;

import com.smelldetection.base.item.SharedLibraryItem;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
/**
 * @field: sharedLibraries: contains all the libraries shared by multiple microservices and their respective dependencies.
 *          key->the library's name;value(SharedLibraryItem)->a set of microservices depending on this library
 */
public class SharedLibraryContext {
    public boolean status;
    public Map<String, SharedLibraryItem> sharedLibraries;

    public SharedLibraryContext(){
        sharedLibraries = new HashMap<>();
    }

    public void addItem(SharedLibraryItem sharedLibraryItem){
        this.sharedLibraries.put(sharedLibraryItem.getSharedLibraryName(), sharedLibraryItem);
    }

}
