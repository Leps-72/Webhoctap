import requests
import re

session = requests.Session()
r_login = session.get('http://localhost:8080/login')
csrf_token = re.search(r'name="_csrf"\s+value="([^"]+)"', r_login.text).group(1)

data = {
    'username': 'teacher@example.com',
    'password': 'password',
    '_csrf': csrf_token
}

r_post = session.post('http://localhost:8080/login', data=data)
if 'B' in r_post.text or 'Dashboard' in r_post.text:
    print('Logged in successfully!')
else:
    data['password'] = '123456'
    session = requests.Session()
    r_login = session.get('http://localhost:8080/login')
    csrf_token = re.search(r'name="_csrf"\s+value="([^"]+)"', r_login.text).group(1)
    data['_csrf'] = csrf_token
    r_post = session.post('http://localhost:8080/login', data=data)
    print('Logged in with 123456?', 'Dashboard' in r_post.text or 'teacher' in r_post.url)

r_courses = session.get('http://localhost:8080/teacher/courses')
print('Courses status:', r_courses.status_code)
if r_courses.status_code != 200:
    print('COURSES ERROR:')
    print(r_courses.text[:500])

r_quizzes = session.get('http://localhost:8080/teacher/quizzes')
print('Quizzes status:', r_quizzes.status_code)
if r_quizzes.status_code != 200:
    print('QUIZZES ERROR:')
    print(r_quizzes.text[:500])

r_materials = session.get('http://localhost:8080/teacher/materials')
print('Materials status:', r_materials.status_code)
if r_materials.status_code != 200:
    print('MATERIALS ERROR:')
    print(r_materials.text[:500])
