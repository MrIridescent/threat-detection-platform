-- Sample ML Models for Project Iris
INSERT INTO ml_models (name, version, model_type, description, file_path, accuracy, precision, recall, f1_score, created_at, active)
VALUES
    ('Neural Text Analyzer', '1.0.0', 'NEURAL_NETWORK', 'Deep learning model for analyzing email text content for phishing indicators', '/models/neural_text_analyzer.pb', 0.92, 0.89, 0.94, 0.91, CURRENT_TIMESTAMP, true),
    ('Header Pattern Detector', '1.2.1', 'RANDOM_FOREST', 'Random forest model for detecting suspicious patterns in email headers', '/models/header_pattern_detector.pkl', 0.88, 0.91, 0.85, 0.88, CURRENT_TIMESTAMP, true),
    ('Sender Reputation Classifier', '0.9.5', 'GRADIENT_BOOSTING', 'Gradient boosting model for classifying sender reputation based on historical data', '/models/sender_reputation.model', 0.86, 0.84, 0.87, 0.85, CURRENT_TIMESTAMP, true),
    ('URL Risk Analyzer', '1.1.0', 'SVM', 'Support vector machine for analyzing URL risk based on multiple factors', '/models/url_risk_analyzer.model', 0.90, 0.92, 0.88, 0.90, CURRENT_TIMESTAMP, true),
    ('Comprehensive Threat Detector', '2.0.0', 'ENSEMBLE', 'Ensemble model combining multiple detection methods for comprehensive threat assessment', '/models/comprehensive_detector.model', 0.94, 0.93, 0.95, 0.94, CURRENT_TIMESTAMP, true);

-- Sample Phishing Campaigns
INSERT INTO phishing_campaigns (name, description, status, severity, target_demographic, estimated_reach, origin_region, technical_details, created_at, last_updated_at, first_observed_at, last_observed_at)
VALUES
    ('Operation Silver Phish', 'Sophisticated phishing campaign targeting financial institutions with well-crafted emails impersonating banking authorities', 'ACTIVE', 'HIGH', 'Banking employees', 15000, 'Eastern Europe', 'Uses multiple redirect chains and compromised legitimate websites as intermediaries. Final payload is a custom infostealer targeting banking credentials and 2FA seeds.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', -15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    ('Tax Season Scam', 'Widespread campaign exploiting tax filing season with fake tax authority communications', 'ACTIVE', 'MEDIUM', 'General public', 50000, 'North America', 'Sends PDF attachments with embedded macros that drop BazarLoader. Uses legitimate tax authority logos and language patterns.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),

    ('Credential Harvest 2025', 'Targeted spear-phishing campaign against corporate executives using social engineering', 'ESCALATING', 'CRITICAL', 'C-level executives', 500, 'Unknown', 'Highly personalized emails referencing recent company events and executives by name. Links to convincing Microsoft 365 login portals with pixel-perfect replicas. Exfiltrates credentials and session cookies.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    ('Supply Chain Compromise', 'Campaign targeting supply chain software with trojanized updates', 'CONTAINED', 'HIGH', 'Logistics companies', 2000, 'Southeast Asia', 'Compromised legitimate software update servers for specialized logistics software. Delivered backdoored updates signed with stolen certificates.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', -60, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP)),

    ('Cloud Storage Lure', 'Phishing campaign using fake cloud storage notifications', 'MITIGATED', 'MEDIUM', 'Corporate users', 25000, 'Western Europe', 'Uses lookalike domains for popular cloud storage services. Emails notify of shared documents requiring authentication. Harvests credentials and deploys web injects for capturing 2FA.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', -45, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP));

-- Sample Infrastructure Nodes
INSERT INTO infrastructure_nodes (campaign_id, name, node_type, identifier, status, technical_details, mitigation_steps, discovered_at, last_checked_at)
VALUES
    (1, 'Primary Distribution Server', 'EMAIL_SERVER', '185.128.43.62', 'ACTIVE', 'Compromised legitimate server used to distribute initial phishing emails. Utilizes custom scripts to bypass spam filters.', 'Contact host provider with abuse report. Block IP at email gateway.', DATEADD('DAY', -15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (1, 'Landing Page Host', 'LANDING_PAGE', 'secure-banklogin.com', 'ACTIVE', 'Lookalike domain hosting credential harvesting page with TLS certificate. Uses advanced browser fingerprinting to avoid detection tools.', 'Submit domain takedown request. Add to blocklists.', DATEADD('DAY', -14, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (1, 'Data Exfiltration Server', 'DATA_COLLECTION', '45.95.167.22', 'ACTIVE', 'Server receiving harvested credentials and session data. Implements encrypted channels and data obfuscation.', 'Block outbound connections to IP. Report to law enforcement.', DATEADD('DAY', -13, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (2, 'Phishing Email Server', 'EMAIL_SERVER', 'mail.tax-refund-services.com', 'ACTIVE', 'Custom mail server sending tax-themed phishing emails. High sending reputation due to gradual warmup.', 'Block domain and IP range at email gateway.', DATEADD('DAY', -30, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (2, 'Malware Distribution', 'DISTRIBUTION_POINT', 'tax-documents-download.com', 'ACTIVE', 'Server hosting malicious PDF attachments with embedded macros. Uses geofencing to avoid serving malware to security researchers.', 'Add to blocklists. Implement attachment scanning.', DATEADD('DAY', -28, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (3, 'Spear Phishing Infrastructure', 'EMAIL_SERVER', '193.27.14.89', 'ACTIVE', 'Dedicated server for sending highly targeted spear-phishing emails to executives. Uses advanced techniques to bypass security controls.', 'Block IP. Implement additional authentication for executive email accounts.', DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (3, 'Credential Harvesting Portal', 'LANDING_PAGE', 'microsoft365-secure-login.com', 'ACTIVE', 'Pixel-perfect replica of Microsoft 365 login portal with valid TLS certificate. Captures credentials and session data.', 'Domain takedown request. Add to blocklists.', DATEADD('DAY', -4, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (3, 'Command & Control Server', 'COMMAND_CONTROL', '87.249.134.12', 'ACTIVE', 'Server controlling post-compromise activities. Implements encrypted communication with compromised endpoints.', 'Block outbound connections to IP. Hunt for indicators of compromise within network.', DATEADD('DAY', -3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (4, 'Compromised Update Server', 'DISTRIBUTION_POINT', 'updates.legitimate-software.com', 'MITIGATED', 'Legitimate update server compromised to distribute trojanized updates. Used stolen certificates to sign malicious code.', 'Coordinate with vendor to secure update infrastructure. Implement file integrity monitoring.', DATEADD('DAY', -60, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP)),

    (5, 'Phishing Domain', 'LANDING_PAGE', 'secure-cloud-storage.com', 'MITIGATED', 'Domain hosting cloud storage phishing pages targeting multiple popular services. Implements advanced evasion techniques.', 'Domain successfully taken down. Added to global blocklists.', DATEADD('DAY', -45, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP));

-- Sample Attack Vectors
INSERT INTO attack_vectors (campaign_id, name, vector_type, description, status, prevalence, effectiveness, technical_details, mitigation_steps, identified_at, last_observed_at)
VALUES
    (1, 'Banking Authority Impersonation', 'EMAIL', 'Phishing emails impersonating banking regulatory authorities requesting urgent action', 'ACTIVE', 8, 9, 'Emails use official logos, language and formatting consistent with legitimate communications. Contain urgent requests to verify account information due to alleged security incidents.', 'Implement DMARC enforcement. Train users to verify requests through official channels.', DATEADD('DAY', -15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (1, 'Security Alert Lure', 'EMAIL', 'Fake security alert emails claiming account compromise', 'ACTIVE', 7, 8, 'Emails designed to create urgency by claiming account security breach. Contains legitimate-looking security alert formatting and branding.', 'Implement additional email authentication. User awareness training focused on verification procedures.', DATEADD('DAY', -13, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),

    (2, 'Tax Authority Impersonation', 'EMAIL', 'Emails claiming to be from tax authorities with refund notifications or filing requirements', 'ACTIVE', 9, 7, 'Utilizes official tax authority branding and language. References current tax filing deadlines and regulations to appear legitimate.', 'User training specific to tax season scams. Enhanced scanning of tax-related attachments.', DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),

    (2, 'Malicious Tax Document', 'MALICIOUS_DOCUMENT', 'PDF attachments with embedded macros claiming to be tax forms', 'ACTIVE', 8, 8, 'PDF documents contain embedded scripts that execute when opened. Use social engineering to convince users to enable macros or scripts.', 'Block macro-enabled documents. Implement sandboxed document preview.', DATEADD('DAY', -28, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP)),

    (3, 'Executive Spear Phishing', 'EMAIL', 'Highly targeted emails to executives with personalized content', 'ACTIVE', 6, 10, 'Emails reference specific company information, recent events, and personal details of the target. Often appear to come from other executives or board members.', 'Implement executive protection protocols. Enhanced authentication for high-value targets.', DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (3, 'Fake Microsoft 365 Portal', 'WEBSITE', 'Convincing replica of Microsoft 365 login portal', 'ACTIVE', 7, 9, 'Pixel-perfect copy of legitimate Microsoft login page. Implements TLS certificate and convincing URL. Captures credentials and session tokens.', 'Implement phishing-resistant authentication methods. User training on URL verification.', DATEADD('DAY', -4, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

    (4, 'Trojanized Software Update', 'MALICIOUS_DOCUMENT', 'Software updates containing backdoored components', 'MITIGATED', 5, 10, 'Legitimate software updates modified to include malicious components. Signed with stolen certificates to appear legitimate.', 'Implement software supply chain security measures. Verify update integrity through multiple channels.', DATEADD('DAY', -60, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP)),

    (5, 'Cloud Storage Notification', 'EMAIL', 'Fake notifications about shared documents requiring login', 'MITIGATED', 8, 7, 'Emails mimic legitimate cloud storage sharing notifications. Include branding and formatting consistent with real services.', 'Implement additional authentication for cloud storage access. User training on verification of sharing requests.', DATEADD('DAY', -45, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP));
