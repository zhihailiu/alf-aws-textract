package com.acme.ocr.alf;

import java.util.LinkedHashSet;
import java.util.Set;

import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.rendition.RenditionDefinition;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.transaction.TransactionListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannedDocumentAspect extends TransactionListenerAdapter
		implements ContentServicePolicies.OnContentUpdatePolicy {

	private static final Logger logger = LoggerFactory.getLogger(ScannedDocumentAspect.class);

	private static final String KEY_POST_TXN_NODES_PDF_AWS_TEXTRACT = "ScannedDocumentAspect.POST_TXN_NODES_PDF_AWS_TEXTRACT";

	private static QName RENDITION_PDF_AWS_EXTRACT = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
			"pdf-aws-textract");

	public static QName ASPECT_SCANNED = QName.createQName("http://www.acme.org/model/content/1.0", "scanned");

	private PolicyComponent policyComponent;

	private RenditionService renditionService;

	private TransactionService transactionService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setRenditionService(RenditionService renditionService) {
		this.renditionService = renditionService;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void init() {
		logger.debug("init");
		this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onContentUpdate"),
				ASPECT_SCANNED, new JavaBehaviour(this, "onContentUpdate"));
	}

	@Override
	public void onContentUpdate(NodeRef nodeRef, boolean newContent) {
		if (newContent) {
			logger.debug("onContentUpdate, id={}", nodeRef.getId());
			AlfrescoTransactionSupport.bindListener(this);
			this.getPostTxnNodes().add(nodeRef);
		}

	}

	@Override
	public void afterCommit() {
		for (final NodeRef nodeRef : getPostTxnNodes()) {
			RetryingTransactionHelper helper = this.transactionService.getRetryingTransactionHelper();
			helper.doInTransaction(new RetryingTransactionCallback<Object>() {
				@Override
				public Object execute() throws Throwable {
					logger.debug("afterCommit, id={}", nodeRef.getId());
					RenditionDefinition def = renditionService.loadRenditionDefinition(RENDITION_PDF_AWS_EXTRACT);
					renditionService.render(nodeRef, def);
					return null;
				}
			}, false, true);
		}
	}

	private Set<NodeRef> getPostTxnNodes() {
		Set<NodeRef> nodes = AlfrescoTransactionSupport.getResource(KEY_POST_TXN_NODES_PDF_AWS_TEXTRACT);

		if (nodes == null) {
			nodes = new LinkedHashSet<NodeRef>();
			AlfrescoTransactionSupport.bindResource(KEY_POST_TXN_NODES_PDF_AWS_TEXTRACT, nodes);
		}

		return nodes;
	}

}
