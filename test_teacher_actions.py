import requests
import re

session = requests.Session()
r_login = session.get('http://localhost:8080/login')
csrf_token = re.search(r'name="_csrf"\s+value="([^"]+)"', r_login.text).group(1)

data = {'username': 'teacher@example.com', 'password': 'password', '_csrf': csrf_token}
session.post('http://localhost:8080/login', data=data)

urls = [
    '/teacher/courses/new',
    '/teacher/quizzes/new',
    '/teacher/materials/upload',
    '/teacher/question-bank'
]

for url in urls:
    r = session.get('http://localhost:8080' + url)
    print(f'{url}: {r.status_code}')
    if r.status_code != 200:
        print('ERROR:', r.text[:500])
