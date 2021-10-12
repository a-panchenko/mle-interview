import os
import pandas as pd
import requests
import time


ORDERS_INPUT_FILE = './data/orders.csv'
WEBSERVICE_HOST = 'localhost'
OUTPUT_DIR = './results'

def __get_recommendation(row):
    request_payload = {\
        'order_id': row.order_id,\
        'customer_id': row.customer_id,\
        'timestamp': row.timestamp,\
        'sku_code': str(row.sku_code),\
        'zip_code': str(row.zip_code) }
    response = requests.post('http://' + WEBSERVICE_HOST + ':8080/decision/v1', json = request_payload)
    if (response.status_code is 200):
        response_json = response.json()
        return response_json['recommendation']
    else:
        print('Response from the server ' + str(response.status_code))
        return None

if __name__ == '__main__':
    if os.environ.get('ORDERS_INPUT_FILE') is not None:
        ORDERS_INPUT_FILE = os.environ.get('ORDERS_INPUT_FILE')
    if os.environ.get('WEBSERVICE_HOST') is not None:
        WEBSERVICE_HOST = os.environ.get('WEBSERVICE_HOST')
    if os.environ.get('OUTPUT_DIR') is not None:
        OUTPUT_DIR = os.environ.get('OUTPUT_DIR')

    time.sleep(10)

    orders_df = pd.read_csv(ORDERS_INPUT_FILE)

    orders_df['recommended_status'] = orders_df.apply(lambda row: __get_recommendation(row), axis=1)
    orders_df[['order_id', 'recommended_status']].to_json(OUTPUT_DIR + '/results.json', orient='records', lines=True)
