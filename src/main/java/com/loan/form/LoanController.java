package com.loan.form;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.loan.ApplicationSubmitEvent;
import com.loan.model.Applicant;
import com.loan.model.LoanApplication;
import com.loan.service.LoanEventPublisher;
import com.loan.service.LocalFileStorageService;

@Controller
public class LoanController {

	private static final Logger log = LoggerFactory.getLogger(LoanController.class);

	@Autowired
	private LoanEventPublisher publisher;

	@Autowired
	private LocalFileStorageService storage;

	@GetMapping("/")
	public String root() {
		return "redirect:/apply";
	}

	@GetMapping("/apply")
	public String showForm(Model model) {
		model.addAttribute("form", new LoanApplicationForm());
		return "apply";
	}

	@PostMapping("/apply")
	public String submit(@Valid @ModelAttribute("form") LoanApplicationForm form, BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "apply";
		}

		// Publish event
		LoanApplication loanApplication = new LoanApplication();
		loanApplication.setLoanAmount(BigDecimal.valueOf(form.getAmount().doubleValue()));
		loanApplication.setLoanId(UUID.randomUUID().toString());

		Applicant applicant = new Applicant();
		applicant.setFirstName(form.getFirstName());
		applicant.setLastName(form.getLastName());
		applicant.setEmail(form.getEmail());
		
		applicant.setApplicantId(UUID.randomUUID().toString());
		
		try {
			storage.clearFileDirectory("files");

			loanApplication.setPayStubUri(storage.save(form.getPayStub(), "files", null));
			loanApplication.setBankStmtUri(storage.save(form.getBankStatement(), "files", null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loanApplication.setApplicant(applicant);
		ApplicationSubmitEvent event = new ApplicationSubmitEvent(loanApplication);
		publisher.publish(event);

		log.info("Loan application received: {} {} | email={} | phone={} | amount={}", form.getFirstName(),
				form.getLastName(), form.getEmail(), form.getPhone(), form.getAmount());

		model.addAttribute("form", form);
		return "success";
	}
}
