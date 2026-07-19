package com.dereli.melasoft_task;

import com.dereli.melasoft_task.model.InvoiceModel;
import com.dereli.melasoft_task.service.InvoiceService;
import com.dereli.melasoft_task.service.impl.InvoiceServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MelasoftTaskApplication implements CommandLineRunner {

    private final InvoiceService invoiceService;

    public MelasoftTaskApplication(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MelasoftTaskApplication.class, args);
    }

    @Override
    public void run(String... args) {

        if (args.length < 5) {
            System.out.println("Kullanim: java Main <faturaNo> <saticiUlke> <saticiKdvNo> <aliciUlke> <aliciKdvNo>");
            System.exit(1);
        }

        InvoiceModel invoice = new InvoiceModel(
                args[0],
                args[1],
                args[2],
                args[3],
                args[4]
        );

        invoiceService.validateInvoice(invoice);
    }
}
