package com.example.s3files.s3files;

import com.amazonaws.services.s3.AmazonS3;
import com.example.s3files.s3files.RestEndpoint.S3StorageEndpoint;
import com.example.s3files.s3files.model.ObjectName;
import com.example.s3files.s3files.services.AWSS3StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class S3filesApplicationTests {

	@MockBean
	AmazonS3 client;

	@MockBean
	private AWSS3StorageService awsS3StorageService;

	@Autowired
	private MockMvc mockMvc;
	private static ObjectMapper mapper = new ObjectMapper();

	@Autowired
	WebApplicationContext context;

	@InjectMocks
	private S3StorageEndpoint endpoint = new S3StorageEndpoint();

	private InputStream is;

	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(endpoint).build();
		is = endpoint.getClass().getClassLoader().getResourceAsStream("Snake_River.jpg");
	}

	
	  @Test 
	  public void getListTest() throws Exception { List<ObjectName>
	  listObjects = new ArrayList<>();
	  
	  
	  listObjects.add(new ObjectName("TestFile.txt"));
	  
	  try{
	  
			  Mockito.when(awsS3StorageService.getS3Objects()).thenReturn(listObjects);
			  String json = mapper.writeValueAsString(listObjects);
			  mockMvc.perform(MockMvcRequestBuilders.get("/list/files")
					  .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
					  .content(json).accept(MediaType.APPLICATION_JSON))
			  		  .andExpect(MockMvcResultMatchers.jsonPath("$[0].objectName",
			  		  Matchers.equalTo("TestFile.txt")));
	  
	  
	  } catch(Exception ex){ ex.printStackTrace(); } }
	 

	@Test
	public void testUploadFiles() throws Exception {
		
		File file = new File("Snake_River.jpeg");
		
		FileInputStream fileinputstream = new FileInputStream(file);
		
		MockMultipartFile firstFile = new MockMultipartFile("upload", "Snake_River.jpeg", "multipart/form-data", fileinputstream);
		
		HashMap<String, String> contentTypeParams = new HashMap<String, String>();
		contentTypeParams.put("boundary", "265001916915724");
		MediaType mediaType = new MediaType("multipart", "form-data", contentTypeParams);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.multipart("/user-profile/images").file("file", firstFile.getBytes())
						.contentType(mediaType).param("filename", "Snake_River.jpeg"))
				.andExpect(status().is(200)).andReturn();

		assertEquals(200, result.getResponse().getStatus());
		assertNotNull(result.getResponse().getContentAsString());

	}

}
