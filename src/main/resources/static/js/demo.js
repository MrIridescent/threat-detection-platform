// Demo JavaScript for interactive threat detection showcase

let demoData = {
    campaigns: 0,
    threats: 0,
    accuracy: 94.2,
    responseTime: 245
};

// Initialize demo when page loads
document.addEventListener('DOMContentLoaded', function() {
    loadDashboardData();
    startLiveUpdates();
    showWelcomeMessage();
});

// Load initial dashboard data
async function loadDashboardData() {
    try {
        const response = await fetch('/api/v1/demo/dashboard');
        if (response.ok) {
            const data = await response.json();
            updateMetrics(data);
        } else {
            // Fallback to simulated data
            updateMetrics({
                activeCampaigns: 12,
                criticalThreats: 5,
                totalVictims: 15847
            });
        }
    } catch (error) {
        console.log('Using simulated data for demo');
        updateMetrics({
            activeCampaigns: 12,
            criticalThreats: 5,
            totalVictims: 15847
        });
    }
}

// Update dashboard metrics
function updateMetrics(data) {
    document.getElementById('activeCampaigns').textContent = data.activeCampaigns || 12;
    document.getElementById('criticalThreats').textContent = data.criticalThreats || 5;
    
    // Animate the numbers
    animateNumber('activeCampaigns', data.activeCampaigns || 12);
    animateNumber('criticalThreats', data.criticalThreats || 5);
}

// Animate number counting
function animateNumber(elementId, targetValue) {
    const element = document.getElementById(elementId);
    const startValue = 0;
    const duration = 2000;
    const startTime = performance.now();
    
    function updateNumber(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const currentValue = Math.floor(startValue + (targetValue - startValue) * progress);
        
        element.textContent = currentValue;
        
        if (progress < 1) {
            requestAnimationFrame(updateNumber);
        }
    }
    
    requestAnimationFrame(updateNumber);
}

// Start demo function
function startDemo() {
    showAlert('Demo Started!', 'Interactive threat detection demo is now active. Try the buttons below!', 'success');
    loadDashboardData();
}

// Simulate email threat detection
async function simulateEmailThreat() {
    showLoading('Analyzing email with AI models...');
    
    // Simulate API call delay
    await sleep(2000);
    
    const threatData = {
        scenario: 'Sophisticated Phishing Email',
        detectionTime: Math.floor(Math.random() * 500) + 100,
        threatScore: 0.87,
        aiAnalysis: {
            primaryThreat: 'Business Email Compromise (BEC)',
            indicators: [
                'Suspicious sender domain similarity',
                'Urgent language patterns detected',
                'Credential harvesting attempt',
                'Social engineering tactics'
            ],
            attribution: 'APT-29 (Cozy Bear) - 78% confidence',
            similarThreats: ['Campaign-2023-BEC-Finance', 'Operation-DigitalMirage']
        },
        recommendedActions: [
            'Block sender domain immediately',
            'Alert security team',
            'Quarantine similar emails',
            'Update threat intelligence feeds'
        ],
        confidenceLevel: 'HIGH'
    };
    
    displayThreatAnalysis(threatData);
    addLiveAlert('🚨 High-risk email detected and blocked automatically', 'danger');
}

// Show campaign network visualization
async function showCampaignNetwork() {
    showLoading('Generating AI-powered network visualization...');
    
    await sleep(1500);
    
    const networkHtml = `
        <div class="network-visualization">
            <h5><i class="fas fa-project-diagram"></i> Campaign Infrastructure Network</h5>
            <div class="network-viz" id="networkViz">
                <div class="text-center p-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading network...</span>
                    </div>
                    <p class="mt-3">Analyzing infrastructure patterns with Graph Neural Networks...</p>
                </div>
            </div>
            <div class="mt-3">
                <h6>AI Insights:</h6>
                <ul class="list-unstyled">
                    <li><i class="fas fa-check text-success"></i> High network density suggests coordinated infrastructure</li>
                    <li><i class="fas fa-check text-warning"></i> Pattern matches known APT group tactics</li>
                    <li><i class="fas fa-check text-danger"></i> Predicted expansion in next 48-72 hours</li>
                    <li><i class="fas fa-check text-info"></i> Critical nodes identified for priority takedown</li>
                </ul>
            </div>
        </div>
    `;
    
    document.getElementById('demoResults').innerHTML = networkHtml;
    
    // Simulate network loading
    setTimeout(() => {
        generateNetworkVisualization();
    }, 2000);
    
    addLiveAlert('📊 Campaign network analysis completed', 'info');
}

// Run AI prediction analysis
async function runAIPrediction() {
    showLoading('Running ensemble AI models for threat prediction...');
    
    await sleep(2500);
    
    const predictionHtml = `
        <div class="prediction-analysis">
            <h5><i class="fas fa-crystal-ball"></i> AI Prediction Analysis</h5>
            <div class="row">
                <div class="col-md-6">
                    <div class="card bg-light">
                        <div class="card-body">
                            <h6>Campaign Evolution Prediction</h6>
                            <div class="progress mb-2">
                                <div class="progress-bar bg-danger" style="width: 87%">87%</div>
                            </div>
                            <small>Likelihood of campaign expansion within 48 hours</small>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card bg-light">
                        <div class="card-body">
                            <h6>Threat Escalation Score</h6>
                            <div class="progress mb-2">
                                <div class="progress-bar bg-warning" style="width: 73%">73%</div>
                            </div>
                            <small>Probability of threat level increase</small>
                        </div>
                    </div>
                </div>
            </div>
            <div class="mt-3">
                <h6>Predicted Actions:</h6>
                <ul class="list-group list-group-flush">
                    <li class="list-group-item">🔮 Campaign likely to expand infrastructure within 48 hours</li>
                    <li class="list-group-item">🔍 Monitor for new domain registrations in similar patterns</li>
                    <li class="list-group-item">⚠️ Threat level may escalate - increase monitoring frequency</li>
                    <li class="list-group-item">🛡️ Prepare defensive countermeasures</li>
                </ul>
            </div>
            <div class="mt-3">
                <span class="badge bg-success">Confidence: 91%</span>
                <span class="badge bg-info">Model: Gradient Boosting Ensemble</span>
                <span class="badge bg-warning">Prediction Time: 120ms</span>
            </div>
        </div>
    `;
    
    document.getElementById('demoResults').innerHTML = predictionHtml;
    addLiveAlert('🔮 AI prediction analysis completed with 91% confidence', 'success');
}

// Show model performance metrics
async function showModelPerformance() {
    showLoading('Fetching AI model performance metrics...');
    
    await sleep(1800);
    
    const performanceHtml = `
        <div class="model-performance">
            <h5><i class="fas fa-chart-bar"></i> AI Model Performance Dashboard</h5>
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Model</th>
                            <th>Accuracy</th>
                            <th>Precision</th>
                            <th>Recall</th>
                            <th>F1-Score</th>
                            <th>Inference Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><strong>Neural Network Classifier</strong></td>
                            <td><span class="badge bg-success">94%</span></td>
                            <td><span class="badge bg-success">92%</span></td>
                            <td><span class="badge bg-success">96%</span></td>
                            <td><span class="badge bg-success">94%</span></td>
                            <td>45ms</td>
                        </tr>
                        <tr>
                            <td><strong>Campaign Evolution Predictor</strong></td>
                            <td><span class="badge bg-success">87%</span></td>
                            <td><span class="badge bg-success">85%</span></td>
                            <td><span class="badge bg-success">89%</span></td>
                            <td><span class="badge bg-success">87%</span></td>
                            <td>120ms</td>
                        </tr>
                        <tr>
                            <td><strong>Anomaly Detector</strong></td>
                            <td><span class="badge bg-success">91%</span></td>
                            <td><span class="badge bg-success">88%</span></td>
                            <td><span class="badge bg-success">94%</span></td>
                            <td><span class="badge bg-success">91%</span></td>
                            <td>80ms</td>
                        </tr>
                        <tr>
                            <td><strong>Attribution Ensemble</strong></td>
                            <td><span class="badge bg-success">89%</span></td>
                            <td><span class="badge bg-success">91%</span></td>
                            <td><span class="badge bg-success">87%</span></td>
                            <td><span class="badge bg-success">89%</span></td>
                            <td>200ms</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="mt-3">
                <h6>Real-time Metrics:</h6>
                <div class="row">
                    <div class="col-md-3">
                        <div class="text-center">
                            <h4 class="text-primary">1,247</h4>
                            <small>Predictions Today</small>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="text-center">
                            <h4 class="text-success">99.2%</h4>
                            <small>System Uptime</small>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="text-center">
                            <h4 class="text-info">156ms</h4>
                            <small>Avg Response Time</small>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="text-center">
                            <h4 class="text-warning">5</h4>
                            <small>Active Models</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('demoResults').innerHTML = performanceHtml;
    addLiveAlert('📈 Model performance metrics updated', 'info');
}

// Display threat analysis results
function displayThreatAnalysis(threatData) {
    const analysisHtml = `
        <div class="threat-analysis">
            <div class="alert alert-danger">
                <h5><i class="fas fa-exclamation-triangle"></i> Threat Detected: ${threatData.scenario}</h5>
                <p><strong>Detection Time:</strong> ${threatData.detectionTime}ms | <strong>Threat Score:</strong> ${(threatData.threatScore * 100).toFixed(1)}% | <strong>Confidence:</strong> ${threatData.confidenceLevel}</p>
            </div>
            
            <div class="row">
                <div class="col-md-6">
                    <h6>AI Analysis Results:</h6>
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item"><strong>Primary Threat:</strong> ${threatData.aiAnalysis.primaryThreat}</li>
                        <li class="list-group-item"><strong>Attribution:</strong> ${threatData.aiAnalysis.attribution}</li>
                    </ul>
                    
                    <h6 class="mt-3">Threat Indicators:</h6>
                    <ul class="list-unstyled">
                        ${threatData.aiAnalysis.indicators.map(indicator => 
                            `<li><i class="fas fa-check text-danger"></i> ${indicator}</li>`
                        ).join('')}
                    </ul>
                </div>
                
                <div class="col-md-6">
                    <h6>Recommended Actions:</h6>
                    <ul class="list-unstyled">
                        ${threatData.recommendedActions.map(action => 
                            `<li><i class="fas fa-arrow-right text-primary"></i> ${action}</li>`
                        ).join('')}
                    </ul>
                    
                    <h6 class="mt-3">Similar Threats:</h6>
                    <ul class="list-unstyled">
                        ${threatData.aiAnalysis.similarThreats.map(threat => 
                            `<li><i class="fas fa-link text-info"></i> ${threat}</li>`
                        ).join('')}
                    </ul>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('demoResults').innerHTML = analysisHtml;
}

// Generate network visualization
function generateNetworkVisualization() {
    const networkViz = document.getElementById('networkViz');
    networkViz.innerHTML = `
        <div class="p-3">
            <svg width="100%" height="350" id="networkSvg"></svg>
            <div class="mt-2 text-center">
                <span class="badge bg-danger me-2">● Active Threats</span>
                <span class="badge bg-warning me-2">● Monitoring</span>
                <span class="badge bg-success me-2">● Mitigated</span>
                <span class="badge bg-info">● Infrastructure</span>
            </div>
        </div>
    `;
    
    // Simple D3.js network visualization
    const svg = d3.select("#networkSvg");
    const width = networkViz.clientWidth - 40;
    const height = 300;
    
    // Sample network data
    const nodes = [
        {id: "C2", group: 1, type: "C2 Server"},
        {id: "Phish1", group: 2, type: "Phishing Site"},
        {id: "Phish2", group: 2, type: "Phishing Site"},
        {id: "Malware", group: 3, type: "Malware Host"},
        {id: "Redirect", group: 4, type: "Redirector"},
        {id: "Landing", group: 2, type: "Landing Page"}
    ];
    
    const links = [
        {source: "C2", target: "Phish1"},
        {source: "C2", target: "Phish2"},
        {source: "Phish1", target: "Landing"},
        {source: "Phish2", target: "Malware"},
        {source: "Redirect", target: "Landing"}
    ];
    
    const simulation = d3.forceSimulation(nodes)
        .force("link", d3.forceLink(links).id(d => d.id))
        .force("charge", d3.forceManyBody().strength(-300))
        .force("center", d3.forceCenter(width / 2, height / 2));
    
    const link = svg.append("g")
        .selectAll("line")
        .data(links)
        .enter().append("line")
        .attr("stroke", "#999")
        .attr("stroke-width", 2);
    
    const node = svg.append("g")
        .selectAll("circle")
        .data(nodes)
        .enter().append("circle")
        .attr("r", 15)
        .attr("fill", d => {
            const colors = ["#ff4444", "#ffaa44", "#44ff44", "#4444ff"];
            return colors[d.group - 1];
        })
        .call(d3.drag()
            .on("start", dragstarted)
            .on("drag", dragged)
            .on("end", dragended));
    
    const label = svg.append("g")
        .selectAll("text")
        .data(nodes)
        .enter().append("text")
        .text(d => d.id)
        .attr("font-size", "12px")
        .attr("text-anchor", "middle")
        .attr("dy", 4);
    
    simulation.on("tick", () => {
        link
            .attr("x1", d => d.source.x)
            .attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x)
            .attr("y2", d => d.target.y);
        
        node
            .attr("cx", d => d.x)
            .attr("cy", d => d.y);
        
        label
            .attr("x", d => d.x)
            .attr("y", d => d.y);
    });
    
    function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }
    
    function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
    }
    
    function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
}

// Utility functions
function showLoading(message) {
    document.getElementById('demoResults').innerHTML = `
        <div class="text-center p-5">
            <div class="spinner-border text-primary mb-3" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="text-muted">${message}</p>
        </div>
    `;
}

function showAlert(title, message, type) {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <strong>${title}</strong> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    const alertContainer = document.createElement('div');
    alertContainer.innerHTML = alertHtml;
    document.body.insertBefore(alertContainer, document.body.firstChild);
    
    setTimeout(() => {
        alertContainer.remove();
    }, 5000);
}

function addLiveAlert(message, type) {
    const alertsContainer = document.getElementById('liveAlerts');
    const timestamp = new Date().toLocaleTimeString();
    
    const alertHtml = `
        <div class="alert alert-${type} alert-sm mb-2 threat-alert">
            <small><strong>${timestamp}</strong></small><br>
            ${message}
        </div>
    `;
    
    alertsContainer.insertAdjacentHTML('afterbegin', alertHtml);
    
    // Keep only last 5 alerts
    const alerts = alertsContainer.children;
    while (alerts.length > 5) {
        alerts[alerts.length - 1].remove();
    }
}

function showWelcomeMessage() {
    addLiveAlert('🚀 Threat Detection Platform initialized successfully', 'success');
    addLiveAlert('🤖 5 AI models loaded and ready for analysis', 'info');
    addLiveAlert('📊 Real-time monitoring active', 'primary');
}

function startLiveUpdates() {
    // Simulate live updates every 10 seconds
    setInterval(() => {
        const messages = [
            '🔍 Scanning 1,247 emails per minute',
            '🛡️ Blocked 3 phishing attempts in last hour',
            '📈 AI model accuracy improved to 94.3%',
            '🌐 Monitoring 156 active campaigns globally',
            '⚡ Average response time: 234ms'
        ];
        
        const randomMessage = messages[Math.floor(Math.random() * messages.length)];
        addLiveAlert(randomMessage, 'info');
    }, 10000);
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
