package demo.controller;

import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	
	 @RequestMapping("/error")
	 public String errorHandle(HttpServletRequest request, Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		HttpStatus httpStatus;

		if(status==null){
			System.out.println("error status: null");
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			status = 500;			
		}
		else{
			httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
			System.out.println("errorCode: " + status.toString() );
			System.out.println("errorMessage: " + httpStatus.getReasonPhrase());
		}

		model.addAttribute("errorCode", status.toString());
		model.addAttribute("errorMessage", httpStatus.getReasonPhrase());
		model.addAttribute("text", "관리자 페이지");
		model.addAttribute("redirectUrl", "/");
		

		return "error/error_default";
	}
}