FROM python:3.9.19

COPY . .

RUN pip install --no-cache-dir --upgrade -r requirements.txt

EXPOSE 8003

ENTRYPOINT ["python3", "gpt.py"]