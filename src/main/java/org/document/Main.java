package org.document;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS,1,new Semaphore(2));
        Document document = new Document();
        String signature = "....";
        crptApi.createDocument("ApiKey", document, signature);
    }
}