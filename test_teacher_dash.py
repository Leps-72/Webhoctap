import requests
import re
session = requests.Session()
r_login = session.get('http://localhost:8080/login')
csrf_token = re.search(r'name="_csrf"\s+value="([^"]+)"', r_login.text).group(1)
data = {'username': 'teacher@example.com', 'password': 'password', '_csrf': csrf_token}
session.post('http://localhost:8080/login', data=data)
r_dash = session.get('http://localhost:8080/teacher/dashboard')
print('Dashboard status:', r_dash.status_code)
if r_dash.status_code != 200:
    print('ERROR DASHBOARD:')
    print(r_dash.text[:500])
