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
package com.axelor.apps.supplychain.service.batch;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.supplychain.db.ISalesOrder;
import com.axelor.apps.supplychain.db.SalesOrder;
import com.axelor.apps.supplychain.service.SalesOrderInvoiceService;
import com.axelor.db.JPA;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.IException;
import com.axelor.exception.service.TraceBackService;

public class BatchInvoicing extends BatchStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(BatchInvoicing.class);

	
	@Inject
	public BatchInvoicing(SalesOrderInvoiceService salesOrderInvoiceService) {
		
		super(salesOrderInvoiceService);
	}


	@Override
	protected void process() {
		
		int i = 0;
		List<SalesOrder> salesOrderList = SalesOrder.all().filter("self.invoicingTypeSelect = ?1 AND self.statusSelect = ?2 AND self.company = ?3", 
				ISalesOrder.INVOICING_TYPE_SUBSCRIPTION, ISalesOrder.STATUS_VALIDATED, batch.getSupplychainBatch().getCompany()).fetch();

		for (SalesOrder salesOrder : salesOrderList) {

			try {
				
				salesOrderInvoiceService.checkSubscriptionSalesOrder(SalesOrder.find(salesOrder.getId()));
				
				Invoice invoice = salesOrderInvoiceService.runSubscriptionInvoicing(SalesOrder.find(salesOrder.getId()));
				
				if(invoice != null)  {  
					
					updateSalesOrder(salesOrder); 
					LOG.debug("Facture créée ({}) pour le devis {}", invoice.getInvoiceId(), salesOrder.getSalesOrderSeq());	
					i++; 
					
				}

			} catch (AxelorException e) {
				
				TraceBackService.trace(new AxelorException(String.format("Devis %s", SalesOrder.find(salesOrder.getId()).getSalesOrderSeq()), e, e.getcategory()), IException.INVOICE_ORIGIN, batch.getId());
				incrementAnomaly();
				
			} catch (Exception e) {
				
				TraceBackService.trace(new Exception(String.format("Devis %s", SalesOrder.find(salesOrder.getId()).getSalesOrderSeq()), e), IException.INVOICE_ORIGIN, batch.getId());
				
				incrementAnomaly();
				
				LOG.error("Bug(Anomalie) généré(e) pour le devis {}", SalesOrder.find(salesOrder.getId()).getSalesOrderSeq());
				
			} finally {
				
				if (i % 10 == 0) { JPA.clear(); }
	
			}

		}
		
		
	}
	
	
	/**
	 * As {@code batch} entity can be detached from the session, call {@code Batch.find()} get the entity in the persistant context.
	 * Warning : {@code batch} entity have to be saved before.
	 */
	@Override
	protected void stop() {

		String comment = "Compte rendu de génération de facture d'abonnement :\n";
		comment += String.format("\t* %s Devis(s) traité(s)\n", batch.getDone());
		comment += String.format("\t* %s anomalie(s)", batch.getAnomaly());
		
		super.stop();
		addComment(comment);
		
	}

}
