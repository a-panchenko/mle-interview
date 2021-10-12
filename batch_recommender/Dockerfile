FROM python:3.7

WORKDIR /app

ADD requirements.txt /app/requirements.txt
ADD batch_recommender.py /app/batch_recommender.py

RUN pip install -r requirements.txt

CMD ["python", "batch_recommender.py"]
