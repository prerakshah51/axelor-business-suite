/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
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
package com.axelor.apps.marketing.web;

import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.crm.db.Lead;
import com.axelor.apps.crm.db.repo.LeadRepository;
import com.axelor.apps.marketing.db.TargetList;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.studio.service.filter.FilterJpqlService;
import com.google.inject.Inject;

public class TargetListController {
	
	@Inject
	private FilterJpqlService filterJpqlService;
	
	public void openFilteredLeads(ActionRequest request, ActionResponse response) {
		
		TargetList targetList = request.getContext().asType(TargetList.class);
		
		String leadFilers = filterJpqlService.getJpqlFilters(targetList.getLeadFilterList());
		if (leadFilers != null) {
			response.setView(ActionView.define(I18n.get("Leads"))
					.model(Lead.class.getName())
					.add("grid", "lead-grid")
					.add("form", "lead-form")
					.domain(leadFilers)
					.map());
		}
	}
	
	public void openFilteredPartners(ActionRequest request, ActionResponse response) {
		
		TargetList targetList = request.getContext().asType(TargetList.class);
		
		String partnerFilters = filterJpqlService.getJpqlFilters(targetList.getPartnerFilterList());
		if (partnerFilters != null) {
			response.setView(ActionView.define(I18n.get("Partners"))
					.model(Partner.class.getName())
					.add("grid", "partner-grid")
					.add("form", "partner-form")
					.domain(partnerFilters)
					.map());
		}
		
	}
}