package org.example.domains.clientDomain;

import java.util.ArrayList;
import java.util.List;

public class ClientDataProvider {


    public static List<Client> getData(){

        List<Client> clients =new ArrayList<>();
        clients.add(new Client("Serhii","serhii@gmail.com"));
        clients.add(new Client("Tom","tom@ukr.net"));

        return clients;
    }
}
