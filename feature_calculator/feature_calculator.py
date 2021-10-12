import pandas as pd
import os
from datetime import datetime
from pandas.core.frame import DataFrame

INPUT_DIR = '../data'
OUTPUT_DIR = '../feature_storage'


def read_csv_as_df(file: str) -> DataFrame:
    if file is None or len(file) < 1:
        raise Exception('Invalid input file ' + file)
    else:
        return pd.read_csv(INPUT_DIR + '/' + file)


def __calculate_hour_of_day(orders: DataFrame) -> DataFrame:
    orders['order_hour_of_day'] = orders['timestamp'].transform(__parse_hour_from_timestamp_str)
    return orders


def __parse_hour_from_timestamp_str(timestamp: str) -> int:
    return __parse_timestamp(timestamp).hour


def __parse_timestamp(timestamp: str) -> datetime:
    return datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S')


def __calculate_zip_code_available(df: DataFrame) -> DataFrame:
    df['zip_code_available'] = df.apply(lambda row: __parse_timestamp(row.timestamp) >= __parse_timestamp(row.available_from), axis=1)
    return df


def calculate_features(orders_df: DataFrame, payments_df: DataFrame, zip_codes_df: DataFrame, inventory_df: DataFrame) -> DataFrame:
    orders_subset = __calculate_hour_of_day(orders_df[['order_id', 'timestamp', 'sku_code', 'zip_code']])
    
    orders_with_inventory = orders_subset.merge(inventory_df, on=['sku_code', 'timestamp'])

    orders_with_inventory_and_payment = orders_with_inventory.merge(payments_df[['order_id', 'payment_status']], on=['order_id'])

    result_df = orders_with_inventory_and_payment.merge(zip_codes_df, on='zip_code')

    result_df = __calculate_zip_code_available(result_df)

    features_df = result_df[['order_id', 'inventory', 'order_hour_of_day', 'zip_code_available', 'payment_status']]

    return features_df

def write_features(features: DataFrame):
    features.to_json(OUTPUT_DIR + '/features', orient='records', lines=True)


if __name__ == '__main__':
    if os.environ.get('INPUT_DIR') is not None:
        INPUT_DIR = os.environ.get('INPUT_DIR')
    if os.environ.get('OUTPUT_DIR') is not None:
        OUTPUT_DIR = os.environ.get('OUTPUT_DIR')

    inventory_df = read_csv_as_df('inventory.csv')
    orders_df = read_csv_as_df('orders.csv')
    payments_df = read_csv_as_df('payments.csv')
    zip_codes_df = read_csv_as_df('zip_codes.csv')

    features = calculate_features(orders_df, payments_df, zip_codes_df, inventory_df)

    write_features(features)


