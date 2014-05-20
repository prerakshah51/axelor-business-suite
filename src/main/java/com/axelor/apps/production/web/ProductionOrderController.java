/**
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
 *
 * The contents of this file are subject to the Common Public
 * Attribution License Version 1.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://license.axelor.com/.
 *
 * The License is based on the Mozilla Public License Version 1.1 but
 * Sections 14 and 15 have been added to cover use of software over a
 * computer network and provide for limited attribution for the
 * Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is part of "Axelor Business Suite", developed by
 * Axelor exclusively.
 *
 * The Original Developer is the Initial Developer. The Initial Developer of
 * the Original Code is Axelor.
 *
 * All portions of the code written by Axelor are
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
 */
package com.axelor.apps.production.web;

import java.math.BigDecimal;
import java.util.Map;

import javax.inject.Inject;

import com.axelor.apps.base.db.Product;
import com.axelor.apps.production.db.BillOfMaterial;
import com.axelor.apps.production.db.ProductionOrder;
import com.axelor.apps.production.service.ProductionOrderSalesOrderService;
import com.axelor.apps.production.service.ProductionOrderService;
import com.axelor.exception.AxelorException;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class ProductionOrderController {

	@Inject
	ProductionOrderService productionOrderService;
	
	@Inject
	ProductionOrderSalesOrderService productionOrderSalesOrderService;
	
	public void propagateIsToInvoice (ActionRequest request, ActionResponse response) {

		ProductionOrder productionOrder = request.getContext().asType( ProductionOrder.class );

		productionOrderService.propagateIsToInvoice(ProductionOrder.find(productionOrder.getId()));
		
		response.setReload(true);
		
	}
	
	public void generateSalesOrder (ActionRequest request, ActionResponse response) throws AxelorException {

		ProductionOrder productionOrder = request.getContext().asType( ProductionOrder.class );

		productionOrderSalesOrderService.createSalesOrder(ProductionOrder.find(productionOrder.getId()));
		
		response.setReload(true);
		
	}
	
	public void addManufOrder (ActionRequest request, ActionResponse response) throws AxelorException {

		Context context = request.getContext();
		
		if(context.get("qty") == null || new BigDecimal((String)context.get("qty")).compareTo(BigDecimal.ZERO) <= 0)  {
			response.setFlash("Veuillez entrer une quantité positive !");
		}
		else if(context.get("billOfMaterial") == null)  {
			response.setFlash("Veuillez sélectionner une nomenclature !");
		}
		else  {
			Map<String, Object> bomContext = (Map<String, Object>) context.get("billOfMaterial");
			BillOfMaterial billOfMaterial = BillOfMaterial.find(((Integer) bomContext.get("id")).longValue());
			
			BigDecimal qty = new BigDecimal((String)context.get("qty"));
			
			Product product = null;
			
			if(context.get("product") == null)  {
				Map<String, Object> productContext = (Map<String, Object>) context.get("product");
				product = Product.find(((Integer) productContext.get("id")).longValue());
			}
			else  {
				product = billOfMaterial.getProduct();
			}
			
			
			ProductionOrder productionOrder = request.getContext().asType( ProductionOrder.class );
			
			productionOrderService.addManufOrder(ProductionOrder.find(productionOrder.getId()), product, billOfMaterial, qty);
			
			response.setReload(true);
		}
		
	}
	
}
