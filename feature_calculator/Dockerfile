FROM python:3.7

WORKDIR /app

ADD requirements.txt /app/requirements.txt
ADD feature_calculator.py /app/feature_calculator.py
ADD feature_calculator_test.py /app/feature_calculator_test.py

RUN pip install -r requirements.txt

RUN python feature_calculator_test.py
CMD ["python", "feature_calculator.py"]
