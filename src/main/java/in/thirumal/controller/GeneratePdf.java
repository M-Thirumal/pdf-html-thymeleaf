/**
 * 
 */
package in.thirumal.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

/**
 * @author thirumal
 *
 */
@RestController
@RequestMapping("generate")
public class GeneratePdf {

	@GetMapping("/pdf")
	public ResponseEntity<ByteArrayResource> generatePdf() throws IOException, DocumentException {
		String html = parseThymeleafTemplate();
		byte[] pdf = generatePdfFromHtml(html);
		return ResponseEntity.ok().contentLength(pdf.length).contentType(MediaType.APPLICATION_PDF)
				.header("Content-Disposition", "attachment; filename=" + ("Thymeleaf" + ".pdf"))
				.body(new ByteArrayResource(pdf));
	}
	
	public byte[] generatePdfFromHtml(String html) throws IOException, DocumentException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(html);
		renderer.layout();
		renderer.createPDF(outputStream);
		return outputStream.toByteArray();
	}

	private String parseThymeleafTemplate() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		Context context = new Context();
		context.setVariable("to", "test PDF");

		return templateEngine.process("test", context);
	}
}
