$base = $env:API_BASE -or 'http://localhost:3001'

Write-Host "Health check:`n"
Invoke-RestMethod -Uri "$base/health"

Write-Host "`nExposure assessment:`n"
Invoke-RestMethod -Method Post -Uri "$base/api/assessments/exposure" -Headers @{ 'x-demo-user-id'='alice'; 'Content-Type'='application/json' } -Body (@{ publicInstagram = $true; locationSharing = $false } | ConvertTo-Json)

Write-Host "`nActivate emergency:`n"
Invoke-RestMethod -Method Post -Uri "$base/api/emergency/activate" -Headers @{ 'x-demo-user-id'='alice'; 'Content-Type'='application/json' } -Body (@{ reason='Threat received'; exposureAnswers=@{ publicInstagram=$true }; threatAnswers=@{ directThreats=$true } } | ConvertTo-Json)

Write-Host "`nList evidence:`n"
Invoke-RestMethod -Uri "$base/api/evidence" -Headers @{ 'x-demo-user-id'='alice' }

Write-Host "`nList audit logs:`n"
Invoke-RestMethod -Uri "$base/api/audit" -Headers @{ 'x-demo-user-id'='alice' }

Write-Host "`nNote: to test file upload, use the curl multipart example in the README."
