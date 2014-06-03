/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2012-2014 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.supplychain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.service.invoice.generator.InvoiceGenerator;
import com.axelor.apps.account.service.invoice.generator.InvoiceLineGenerator;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.organisation.db.Task;
import com.axelor.apps.supplychain.db.PurchaseOrder;
import com.axelor.apps.supplychain.db.SalesOrder;
import com.axelor.apps.supplychain.db.StockMove;
import com.axelor.apps.supplychain.db.StockMoveLine;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.IException;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class StockMoveInvoiceService {
	
	@Inject
	private SalesOrderInvoiceService salesOrderInvoiceService;
	
	@Inject
	private PurchaseOrderInvoiceService purchaseOrderInvoiceService;
	
	@Transactional(rollbackOn = {AxelorException.class, Exception.class})
	public Invoice createInvoiceFromSalesOrder(StockMove stockMove, SalesOrder salesOrder) throws AxelorException  {
		
		InvoiceGenerator invoiceGenerator = salesOrderInvoiceService.createInvoiceGenerator(salesOrder);
		
		Invoice invoice = invoiceGenerator.generate();
		
		invoiceGenerator.populate(invoice, this.createInvoiceLines(invoice, stockMove.getStockMoveLineList()));
		
		if (invoice != null) {
		
			this.extendInternalReference(stockMove, invoice);
			
			stockMove.setInvoice(invoice);
			stockMove.save();
		}
		return invoice;
		
	}

	@Transactional(rollbackOn = {AxelorException.class, Exception.class})
	public Invoice createInvoiceFromPurchaseOrder(StockMove stockMove, PurchaseOrder purchaseOrder) throws AxelorException  {
		
		InvoiceGenerator invoiceGenerator = purchaseOrderInvoiceService.createInvoiceGenerator(purchaseOrder);
		
		Invoice invoice = invoiceGenerator.generate();
		
		invoiceGenerator.populate(invoice, this.createInvoiceLines(invoice, stockMove.getStockMoveLineList()));
		
		if (invoice != null) {
			
			this.extendInternalReference(stockMove, invoice);
			
			stockMove.setInvoice(invoice);
			stockMove.save();
		}
		return invoice;	
	}
	
	
	public Invoice extendInternalReference(StockMove stockMove, Invoice invoice)  {
		
		invoice.setInternalReference(stockMove.getStockMoveSeq()+":"+invoice.getInternalReference());
		
		return invoice;
	}
	
	
	private List<InvoiceLine> createInvoiceLines(Invoice invoice,
			List<StockMoveLine> stockMoveLineList) throws AxelorException {
		
		List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
		
		for (StockMoveLine stockMoveLine : stockMoveLineList) {
			invoiceLineList.addAll(this.createInvoiceLine(invoice, stockMoveLine));
		}
		
		return invoiceLineList;
	}

	private List<InvoiceLine> createInvoiceLine(Invoice invoice, StockMoveLine stockMoveLine) throws AxelorException {
		
		Product product = stockMoveLine.getProduct();
		
		if (product == null)
			throw new AxelorException(String.format("Produit incorrect dans le mouvement de stock %s ", stockMoveLine.getStockMove().getStockMoveSeq()), IException.CONFIGURATION_ERROR);

		Task task = null;
		if(invoice.getProject() != null)  {
			task = invoice.getProject().getDefaultTask();
		}
		
		InvoiceLineGenerator invoiceLineGenerator = new InvoiceLineGenerator(invoice, product, product.getName(), stockMoveLine.getPrice(), 
				product.getDescription(), stockMoveLine.getQty(), stockMoveLine.getUnit(), task, product.getInvoiceLineType(), BigDecimal.ZERO, 0, null, false)  {
			@Override
			public List<InvoiceLine> creates() throws AxelorException {
				
				InvoiceLine invoiceLine = this.createInvoiceLine();
				
				List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
				invoiceLines.add(invoiceLine);
				
				return invoiceLines;
			}
		};
		
		return invoiceLineGenerator.creates();
	}
}
