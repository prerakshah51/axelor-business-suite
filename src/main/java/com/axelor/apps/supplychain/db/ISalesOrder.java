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
package com.axelor.apps.supplychain.db;

public interface ISalesOrder {

	
	/**
	 * Static salesOrder status select
	 */

	static final int STATUS_DRAFT = 1;
	static final int STATUS_CONFIRMED = 2;
	static final int STATUS_VALIDATED = 3;
	static final int STATUS_CANCELED = 4;

	
	/**
	 * Static salesOrder invoicingTypeSelect
	 */
	static final int INVOICING_TYPE_PER_ORDER = 1;
	static final int INVOICING_TYPE_WITH_PAYMENT_SCHEDULE = 2;
	static final int INVOICING_TYPE_PER_TASK = 3;
	static final int INVOICING_TYPE_PER_SHIPMENT = 4;
	static final int INVOICING_TYPE_FREE = 5;
	static final int INVOICING_TYPE_SUBSCRIPTION = 6;
	
}
