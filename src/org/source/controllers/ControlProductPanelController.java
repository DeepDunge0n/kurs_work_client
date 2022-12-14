package org.source.controllers;

import java.net.ConnectException;

import javax.swing.JOptionPane;

import org.fr2eman.send.DataResponse;
import org.source.ApplicationUI;
import org.source.models.Product;
import org.source.network.ServerFacade;
import org.source.service.CacheService;
import org.source.views.CreateProductPanel;
import org.source.views.ProductDetailsPanel;
import org.source.views.base.BasePanel;

public class ControlProductPanelController {
	
	private static ControlProductPanelController instance = new ControlProductPanelController();
	
	public static ControlProductPanelController getInstance() {
		return instance;
	}
	
	public void search(BasePanel panel) {
		
		DataResponse response = null;
		try {
			response = ServerFacade.getInstance().requestProducts();
		} catch(ConnectException e) {
			JOptionPane.showMessageDialog(ApplicationUI.getInstance(), "Нет соединения с сервером",
					"Ошибка соединения", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(response.getError()) {
			JOptionPane.showMessageDialog(ApplicationUI.getInstance(), 
					response.getMessage());
			return;
		}
		CacheService.getInstance().clearProducts();
		int numberProducts = Integer.valueOf(response.getData().get("number"));
		for(int i = 1; i <= numberProducts; i++) {
			int id = Integer.valueOf(response.getData().get("id_product" + i));
			String name = response.getData().get("name_product" + i);
			int number = Integer.valueOf(response.getData().get("number_product" + i));
			int price = Integer.valueOf(response.getData().get("price_product" + i));
			CacheService.getInstance().addProductsList(new Product(
					id, name, price, number));
		}
		panel.updatePanel();
	}
	
	public void description() {
		
		DataResponse response = null;
		try {
			response = ServerFacade.getInstance().requestDescriptionProduct();
		} catch(ConnectException e) {
			JOptionPane.showMessageDialog(ApplicationUI.getInstance(), "Нет соединения с сервером",
					"Ошибка соединения", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(response.getError()) {
			JOptionPane.showMessageDialog(ApplicationUI.getInstance(), 
					response.getMessage());
			return;
		}
		
		CacheService.getInstance().setNameProduct(response.getData().get("name_product"));
		CacheService.getInstance().setNumberProduct(response.getData().get("number_product"));
		CacheService.getInstance().setPriceProduct(response.getData().get("price_product"));
		if(Boolean.valueOf(response.getData().get("is_description")))
			CacheService.getInstance().setDescriptionProduct(response.getData().get("description"));
		else CacheService.getInstance().setDescriptionProduct("");
		ApplicationUI.getInstance().push(new ProductDetailsPanel());
	}
	
	public void addProduct() {
		ApplicationUI.getInstance().push(new CreateProductPanel());
	}
	
	public void back() {
		ApplicationUI.getInstance().pop();
	}
}
