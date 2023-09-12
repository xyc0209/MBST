package com.badsmell.base;
import lombok.*;



@Data
@NoArgsConstructor
public class RequestItem {
    public String servicesPath;
    //can be replaced with annotation @AllArgsConstructor
    public RequestItem(String servicesPath){
        this.servicesPath = servicesPath;
    }

}
