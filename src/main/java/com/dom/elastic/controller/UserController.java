package com.dom.elastic.controller;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dom.elastic.domain.User;

@RestController
@RequestMapping("elastic")
public class UserController {

	@Autowired
	private Client client;
	
	@PostMapping()
	public String create(@RequestBody User user) {
		IndexResponse response = null;
		try {
			response = client.prepareIndex("users", "employee", user.getUserId())
					.setSource(jsonBuilder()
							.startObject()
							.field("name", user.getName())
							.field("userSettings", user.getUserSettings())
							.endObject()).get();
			
			System.out.println("response id: " + response.getId() + response.getIndex());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return response.getResult().toString();
	}
	
	@GetMapping("/view/{id}")
	public Map<String, Object> view(@PathVariable final String id){
		GetResponse response = client.prepareGet("users", "employee", id).get();
		return response.getSource();
	}
	
	@GetMapping("/view/name/{field}")
	public Map<String, Object> searchByName(@PathVariable final String field){
		Map<String, Object> map = null;
		SearchResponse response = client.prepareSearch("users")
				.setTypes("employee")
				.setSearchType(SearchType.QUERY_AND_FETCH)
				.setQuery(QueryBuilders.matchQuery("name", field))
				.get();
		
		List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
		map = searchHits.get(0).getSourceAsMap();
		
		return map;
	}
	
	@PutMapping("/{id}")
	public String update(@PathVariable final String id, 
			@RequestBody User user) {
		UpdateRequest updateRequest = new UpdateRequest();
		
		try {
			updateRequest.index("users")
				.type("employee")
				.id(id)
				.doc(jsonBuilder()
						.startObject()
						.field("name", user.getName())
						.field("userSettings", user.getUserSettings())
						.endObject());
			
			UpdateResponse updateResponse = client.update(updateRequest).get();
			
			return updateResponse.status().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Exception";
	}
}
