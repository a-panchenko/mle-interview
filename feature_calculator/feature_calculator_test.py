import pytest
import datetime
import pandas as pd

import feature_calculator


def test_parse_timestamp():
    assert feature_calculator.__parse_timestamp('2021-01-30 13:04:12') == datetime.datetime(2021, 1, 30, 13, 4, 12)
    assert feature_calculator.__parse_timestamp('2001-12-31 23:59:59') == datetime.datetime(2001, 12, 31, 23, 59, 59)
    with pytest.raises(ValueError):
        feature_calculator.__parse_timestamp('asdfasfbjl')

def test_parse_hour_from_timestamp_str():
    assert feature_calculator.__parse_hour_from_timestamp_str('2021-01-30 13:04:12') == 13
    assert feature_calculator.__parse_hour_from_timestamp_str('2021-01-30 00:04:12') == 0
    assert feature_calculator.__parse_hour_from_timestamp_str('2021-01-30 01:04:12') == 1
    assert feature_calculator.__parse_hour_from_timestamp_str('2021-01-30 23:04:12') == 23

def test_calculate_hour_of_day1():
    ids = ['1', '2', '3', '4']
    timestamps = ['2021-01-30 13:04:12', '2021-01-30 00:04:12', '2021-01-30 01:04:12', '2021-01-30 23:04:12']
    expected_hours_of_day = pd.Series([13, 0, 1, 23])
    input = pd.DataFrame({'id': ids, 'timestamp': timestamps})
    
    output = feature_calculator.__calculate_hour_of_day(input)
    
    assert output['order_hour_of_day'].equals(expected_hours_of_day)

def test_calculate_zip_code_available():
    timestamps = ['2021-05-01 16:00:00', '2021-06-30 01:00:00', '2021-01-01 00:00:00']
    available_from = ['2021-05-01 16:00:01', '2021-06-30 00:00:00', '2020-12-31 23:59:59']
    input = pd.DataFrame({'timestamp': timestamps, 'available_from': available_from})

    expected_zip_code_available_series = pd.Series([False, True, True])

    output = feature_calculator.__calculate_zip_code_available(input)

    assert output['zip_code_available'].equals(expected_zip_code_available_series)

def test_calculate_features():
    orders_ids = ['1', '2', '3']
    orders_timestamps = ['2021-05-01 16:00:00', '2021-06-30 01:00:00', '2021-01-01 00:00:00']
    orders_zip_codes = ['0001', '0002', '0003']
    skus = ['01', '02', '03']
    inventory = [10, 20, 30]
    payment_statuses = ['OK', 'VERIFY_BANK_DETAILS', 'FAILED']

    orders_input = pd.DataFrame({'order_id': orders_ids, 'sku_code': skus, 'timestamp': orders_timestamps, 'zip_code': orders_zip_codes})
    payments_input = pd.DataFrame({'order_id': orders_ids, 'payment_status': payment_statuses})
    inventory_input = pd.DataFrame({\
        'sku_code': skus + skus, \
        'timestamp': orders_timestamps + ['2021-12-12 23:59:59', '2021-11-11 00:00:00', '2021-10-10 00:00:00'], \
        'inventory': inventory + [40, 50, 60]
    })
    zip_codes_input = pd.DataFrame({'zip_code': orders_zip_codes, 'available_from': ['2021-05-05 00:00:00', '2021-06-29 01:00:00', '2021-01-01 00:00:00']})

    expected_output = pd.DataFrame({\
        'order_id': orders_ids, \
        'inventory': inventory, \
        'order_hour_of_day': [16, 1, 0], \
        'zip_code_available': [False, True, True], \
        'payment_status': payment_statuses
    })

    output = feature_calculator.calculate_features(orders_input, payments_input, zip_codes_input, inventory_input)
    
    assert output.equals(expected_output)


if __name__ == "__main__":
    pytest.main()
