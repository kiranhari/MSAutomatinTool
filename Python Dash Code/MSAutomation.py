#!/usr/bin/python
# -*- coding: utf-8 -*-
import dash
import dash_core_components as dcc
import dash_html_components as html
from dash.dependencies import Input, Output
from xlrd import open_workbook
from scp import SCPClient
import pandas as pd
import dash_table_experiments as dt
import time
import sys
from datetime import datetime
from pytz import timezone
from os import listdir

external_stylesheets =['C:/Users/KiranSuresh/Documents/SametimeFileTransfers/Dash.css']
# ['https://codepen.io/chriddyp/pen/bWLwgP.css']
#


app = dash.Dash(__name__, external_stylesheets=external_stylesheets)

# all imports are here

# app = dash.Dash(name)

server = app.server

india = timezone('Asia/Kolkata')
india_time = datetime.now(india)
server = app.server

today = datetime.today()
dateFolder=str(today.year)+'/'+str(datetime.now().strftime('%m'))+'/'+str(datetime.now().strftime('%d'))+'/'
path_to_chr_xls_files = 'C:/MSAutomation/CHRMonitoringFiles/Files/'+dateFolder
path_to_xls_files = 'C:/MSAutomation/TBMonitoringFiles/Files'




# CHRMonitoringStat20190220.xls
# CHROutputResultException2019022014.xls
# CHROutputResult2019022014.xls







#sheet_to_df_map2 = pd.ExcelFile(file)


Title = 'MS Automation Dash'
app.layout = html.Div(children=[html.H1(children=Title),
                      dcc.Dropdown(id='input',
                      options=[{'label': 'MS - {}'.format(i),
                      'value': i} for i in ['TB-Dash', 'TB-Exception','TB-Agents',
                      'TB-NonAppendQuery','CHR-Dash', 'CHR-Exception',
                      'CHR-NonAppendQuery']]), html.Div(id='output-graph')])  


@app.callback(Output(component_id='output-graph',
              component_property='children'),
              [Input(component_id='input', component_property='value')])
def update_value(input_data):
    if input_data == 'CHR-Dash':
        listappendfiles = [f for f in listdir(path_to_chr_xls_files)
                   if f.endswith('.xls')
                   & f.startswith('CHRMonitoringStat')]
        AppendDate = [i[:-4] for i in listappendfiles]
        n = len(AppendDate)
        max = AppendDate[0]
        for i in range(1, n):
            if AppendDate[i] > max:
                max = AppendDate[i]

        file = path_to_chr_xls_files + '/' +max + '.xls'
        print (file)
        wb = open_workbook(file)
        sheet = wb.sheet_by_index(0)
        number_of_rows = sheet.nrows
        number_of_columns = sheet.ncols
        time = []
        items = []
        rows = []
        createOrder = []
        ScheduledOrder = []
        ReleaseOrder = []
        ShippedOrder = []
        CanceledOrder = []
        ReturnCreated = []
        for col in range(1, number_of_columns):
            values = []
            for row in range(0, number_of_rows):
                value = sheet.cell(row, col).value
                try:
                    if row == 0:
                        time.append(value)
                    if row == 1:
                        createOrder.append(value)
                    if row == 2:
                        ScheduledOrder.append(value)
                    if row == 3:
                        ReleaseOrder.append(value)
                    if row == 4:
                        ShippedOrder.append(value)
                    if row == 5:
                        CanceledOrder.append(value)
                    if row == 6:
                        ReturnCreated.append(value)
                except ValueError:

                       # switch_func(row,value)
                       # value = str(int(value))
                        # print value

                    pass
                finally:
                    values.append(value)

    # print (time)

        return dcc.Graph(id='example-graph', figure={'data': [{
            'x': time,
            'y': createOrder,
            'type': 'line',
            'name': 'CreateOrder',
            'id': 'live-update-graph',
            }, {
            'x': time,
            'y': ReleaseOrder,
            'type': 'line',
            'name': 'ReleaseOrder',
            }, {
            'x': time,
            'y': CanceledOrder,
            'type': 'line',
            'name': 'CanceledOrder',
            }, {
            'x': time,
            'y': ReturnCreated,
            'type': 'line',
            'name': 'ReturnCreated',
            }], 'layout': {'title': 'MSAutomation CHR-Order Stat'}})   
    elif input_data == 'CHR-Exception':
        listExceptionfiles = [f for f in listdir(path_to_chr_xls_files)
                      if f.endswith('.xls')
                      & f.startswith('CHRException')]
        AppendDate = [i[:-4] for i in listExceptionfiles]
        n = len(AppendDate)
        max = AppendDate[0]
        for i in range(1, n):
            if AppendDate[i] > max:
                max = AppendDate[i]
        ExceptionFile = path_to_chr_xls_files + '/' +max + '.xls'
        print (ExceptionFile)
        sheet_to_df_map = pd.ExcelFile(ExceptionFile)
        numberOfSheets=len(sheet_to_df_map.sheet_names)
        print (numberOfSheets)
        Title='CHR- Exception List'
        return html.Div(children=[html.H1(children=Title),dt.DataTable(rows=sheet_to_df_map.parse(0).to_dict('records'
                        ),filterable=True,sortable=True,id='datatable'),dt.DataTable(rows=sheet_to_df_map.parse(1).to_dict('records'
                        ),filterable=True,sortable=True,id='datatableSQL')])
    elif input_data == 'CHR-NonAppendQuery':
        listNonAppendFiles = [f for f in listdir(path_to_chr_xls_files)
                      if f.endswith('.xls')
                      & f.startswith('CHROutputResult')]
        AppendDate = [i[:-4] for i in listNonAppendFiles]
        n = len(AppendDate)
        max = AppendDate[0]
        for i in range(1, n):
            if AppendDate[i] > max:
                max = AppendDate[i]
        print (max)
        OutputFile = path_to_chr_xls_files + '/' + max + '.xls'
        print (OutputFile)
        sheet_to_df_map1 = pd.ExcelFile(OutputFile)
        Title='CHR- NonAppend Result List'
        return html.Div(children=[html.H1(children=Title),dt.DataTable(rows=sheet_to_df_map1.parse(2).to_dict('records'
                        ), id='datatable1'),dt.DataTable(rows=sheet_to_df_map1.parse(1).to_dict('records'),id='datable30')])
    elif input_data == 'TB-Dash':
        print(path_to_xls_files)
        listappendfiles = [f for f in listdir(path_to_xls_files)
                   if f.endswith('.xls')
                   & f.startswith('TBMonitoringStat')]
        AppendDate = [i[:-4] for i in listappendfiles]
        n = len(AppendDate)
        max = AppendDate[0]
        for i in range(1, n):
            if AppendDate[i] > max:
                max = AppendDate[i]

        file = path_to_xls_files + '/' +max + '.xls'
        print (file)
        wb = open_workbook(file)
        sheet = wb.sheet_by_index(0)
        number_of_rows = sheet.nrows
        number_of_columns = sheet.ncols
        time = []
        items = []
        rows = []
        createOrder = []
        ScheduledOrder = []
        ReleaseOrder = []
        ShippedOrder = []
        CanceledOrder = []
        ReturnCreated = []
        for col in range(1, number_of_columns):
            values = []
            for row in range(0, number_of_rows):
                value = sheet.cell(row, col).value
                try:
                    if row == 0:
                        time.append(value)
                    if row == 1:
                        createOrder.append(value)
                    if row == 2:
                        ScheduledOrder.append(value)
                    if row == 3:
                        ReleaseOrder.append(value)
                    if row == 4:
                        ShippedOrder.append(value)
                    if row == 5:
                        CanceledOrder.append(value)
                    if row == 6:
                        ReturnCreated.append(value)
                except ValueError:

                       # switch_func(row,value)
                       # value = str(int(value))
                        # print value

                    pass
                finally:
                    values.append(value)

    # print (time)

        return dcc.Graph(id='example-graph', figure={'data': [{
            'x': time,
            'y': createOrder,
            'type': 'line',
            'name': 'CreateOrder',
            'id': 'live-update-graph',
            }, {
            'x': time,
            'y': ReleaseOrder,
            'type': 'line',
            'name': 'ReleaseOrder',
            }, {
            'x': time,
            'y': CanceledOrder,
            'type': 'line',
            'name': 'CanceledOrder',
            }, {
            'x': time,
            'y': ReturnCreated,
            'type': 'line',
            'name': 'ReturnCreated',
            }], 'layout': {'title': 'MSAutomation TB-Order Stat'}})
    elif input_data == 'TB-Agents':
        listappendfiles = [f for f in listdir(path_to_xls_files)
                   if f.endswith('.xls')
                   & f.startswith('TBMonitoringStat')]
        AppendDate = [i[:-4] for i in listappendfiles]
        n = len(AppendDate)
        max = AppendDate[0]
        for i in range(1, n):
            if AppendDate[i] > max:
                max = AppendDate[i]

        file = path_to_xls_files + '/' +max + '.xls'
        print (file)
        wb = open_workbook(file)
        sheet = wb.sheet_by_index(1)
        number_of_rows = sheet.nrows
        number_of_columns = sheet.ncols
        time = []
        items = []
        rows = []
        TBIBAAgent = []
        TBCreateOrderIntegServer = []
        TBProcessPaymentAgentServer = []
        TBPOManagementIntegServer = []
        TBOthersPurgeAgent = []
        TBSearchAgentServer = []
        TBOrderProcessingAgentServer = []
        TBAdyenRefundSettlementServer = []
        TBEGCFraudCheckAgentServerCocDataExtractServer = []
        TBScheduleAgentServer = []
        TBProcessGCIntegServer=[]
        TBLoadInvMismatchJMSServer=[]
        TBCatalogAndPriceIntegServer=[]
        TBInvoiceAgentServer=[]
        TBExecuteAdjustInventoryAsyncServer=[]
        TBAsyncRequestProcessorAgent=[]
        TBOrderMonitorAgent=[]
        TBPublishInvoiceAgentServer=[]
        TBProcessDCMessageIntegServer=[]
        TBFullSyncRTAMMonitor=[]
        TBReleaseAgentServer=[]
        TBPurgeAgent=[]
        for col in range(1, number_of_columns):
            values = []
            for row in range(0, number_of_rows):
                value = sheet.cell(row, col).value
                try:
                    if row == 0:
                        time.append(value)	
                    if row == 2:
                        TBIBAAgent.append(value)
                    if row == 3:
                        TBCreateOrderIntegServer.append(value)
                    if row == 4:
                        TBProcessPaymentAgentServer.append(value)
                    if row == 5:
                        TBPOManagementIntegServer.append(value)
                    if row == 6:
                        TBOthersPurgeAgent.append(value)
                    if row == 7:
                        TBSearchAgentServer.append(value)
                    if row == 8:
                        TBOrderProcessingAgentServer.append(value)
                    if row == 9:
                        TBAdyenRefundSettlementServer.append(value)
                    if row == 10:
                        TBEGCFraudCheckAgentServerCocDataExtractServer.append(value)
                    if row == 11:
                        TBScheduleAgentServer.append(value)
                    if row == 12:
                        TBProcessGCIntegServer.append(value)
                    if row == 13:
                        TBLoadInvMismatchJMSServer.append(value)
                    if row == 14:
                        TBCatalogAndPriceIntegServer.append(value)
                    if row == 15:
                        TBInvoiceAgentServer.append(value)
                    if row == 16:
                        TBExecuteAdjustInventoryAsyncServer.append(value)
                    if row == 17:
                        TBAsyncRequestProcessorAgent.append(value)
                    if row == 18:
                        TBOrderMonitorAgent.append(value)
                    if row == 19:
                        TBPublishInvoiceAgentServer.append(value)
                    if row == 20:
                        TBProcessDCMessageIntegServer.append(value)
                    if row == 21:
                        TBFullSyncRTAMMonitor.append(value)
                    if row == 22:
                        TBReleaseAgentServer.append(value)
                    if row == 23:
                        TBFullSyncRTAMMonitor.append(value)
                    if row == 24:
                        TBPurgeAgent.append(value)
                except ValueError:

                       # switch_func(row,value)
                       # value = str(int(value))
                        # print value

                    pass
                finally:
                    values.append(value)

    # print (time)

        return dcc.Graph(id='example-graph', figure={'data': [{
            'x': time,
            'y': TBIBAAgent,
            'type': 'line',
            'name': 'TBIBAAgent',
            'id': 'live-update-graph',
            }, {
            'x': time,
            'y': TBCreateOrderIntegServer,
            'type': 'line',
            'name': 'TBCreateOrderIntegServer',
            }, {
            'x': time,
            'y': TBProcessPaymentAgentServer,
            'type': 'line',
            'name': 'TBProcessPaymentAgentServer',
            }, {
            'x': time,
            'y': TBPOManagementIntegServer,
            'type': 'line',
            'name': 'TBPOManagementIntegServer',
            },{
            'x': time,
            'y': TBOthersPurgeAgent,
            'type': 'line',
            'name': 'TBOthersPurgeAgent',
            },{
            'x': time,
            'y': TBSearchAgentServer,
            'type': 'line',
            'name': 'TBSearchAgentServer',
            },
			{
            'x': time,
            'y': TBOrderProcessingAgentServer,
            'type': 'line',
            'name': 'TBOrderProcessingAgentServer',
            },
			{
            'x': time,
            'y': TBAdyenRefundSettlementServer,
            'type': 'line',
            'name': 'TBAdyenRefundSettlementServer',
            },
			{
            'x': time,
            'y': TBEGCFraudCheckAgentServerCocDataExtractServer,
            'type': 'line',
            'name': 'TBEGCFraudCheckAgentServerCocDataExtractServer',
            },
			{
            'x': time,
            'y': TBScheduleAgentServer,
            'type': 'line',
            'name': 'TBScheduleAgentServer',
            },
			{
            'x': time,
            'y': TBProcessGCIntegServer,
            'type': 'line',
            'name': 'TBProcessGCIntegServer',
            },
			{
            'x': time,
            'y': TBLoadInvMismatchJMSServer,
            'type': 'line',
            'name': 'TBLoadInvMismatchJMSServer',
            },
			{
            'x': time,
            'y': TBCatalogAndPriceIntegServer,
            'type': 'line',
            'name': 'TBCatalogAndPriceIntegServer',
            },
			{
            'x': time,
            'y': TBInvoiceAgentServer,
            'type': 'line',
            'name': 'TBInvoiceAgentServer',
            },
			{
            'x': time,
            'y': TBExecuteAdjustInventoryAsyncServer,
            'type': 'line',
            'name': 'TBExecuteAdjustInventoryAsyncServer',
            },
			{
            'x': time,
            'y': TBAsyncRequestProcessorAgent,
            'type': 'line',
            'name': 'TBAsyncRequestProcessorAgent',
            },
			{
            'x': time,
            'y': TBOrderMonitorAgent,
            'type': 'line',
            'name': 'TBOrderMonitorAgent',
            },{
            'x': time,
            'y': TBPublishInvoiceAgentServer,
            'type': 'line',
            'name': 'TBPublishInvoiceAgentServer',
            },
			{
            'x': time,
            'y': TBProcessDCMessageIntegServer,
            'type': 'line',
            'name': 'TBProcessDCMessageIntegServer',
            },
			{
            'x': time,
            'y': TBFullSyncRTAMMonitor,
            'type': 'line',
            'name': 'TBFullSyncRTAMMonitor',
            },
			{
            'x': time,
            'y': TBReleaseAgentServer,
            'type': 'line',
            'name': 'TBReleaseAgentServer',
            },
			{
            'x': time,
            'y': TBPurgeAgent,
            'type': 'line',
            'name': 'TBPurgeAgent',
            }], 'layout': {'title': 'MSAutomation TB-Agents'}})

    elif input_data == 'TB-Exception':
        listExceptionfiles = [f for f in listdir(path_to_xls_files)
                      if f.endswith('.xls')
                      & f.startswith('TBException')]
        AppendDate = [i[:-4] for i in listExceptionfiles]
        n = len(AppendDate)
        max = AppendDate[0]
        for i in range(1, n):
            if AppendDate[i] > max:
                max = AppendDate[i]
        ExceptionFile = path_to_xls_files + '/' +max + '.xls'
        print (ExceptionFile)
        sheet_to_df_map = pd.ExcelFile(ExceptionFile)
        numberOfSheets=len(sheet_to_df_map.sheet_names)
        x=range(0,numberOfSheets,1)
        #Sheet=""
        #Sheet=Sheet+dt.DataTable(rows=sheet_to_df_map.parse(0).to_dict('records'))
        #print (dt.DataTable(rows=sheet_to_df_map.parse(0).to_dict('records'))
           #for n in x:
         #   sheet=dt.DataTable(rows=sheet_to_df_map.parse(n).to_dict('records'))
          #  print (sheet)
        Title='TB- Exception List'
        return html.Div(children=[html.H1(children=Title),dt.DataTable(rows=sheet_to_df_map.parse(0).to_dict('records'))])
        #return html.Div(children=[html.H1(children=Title),dt.DataTable(rows=sheet_to_df_map.parse(0).to_dict('records'
                       # ), id='datatable'),dt.DataTable(rows=sheet_to_df_map.parse(1).to_dict('records'), id='datatableSql')])
    elif input_data == 'TB-NonAppendQuery':
        listNonAppendFiles = [f for f in listdir(path_to_xls_files)
                      if f.endswith('.xls')
                      & f.startswith('TBOutputResult')]
        AppendDate = [i[:-4] for i in listNonAppendFiles]
        n = len(AppendDate)
        max = AppendDate[0]
        for i in range(1, n):
            if AppendDate[i] > max:
                max = AppendDate[i]
        print (max)
        OutputFile = path_to_xls_files + '/' + max + '.xls'
        #print (OutputFile)
        # (pd.ExcelFile(OutputFile).parse(0).to_dict('records'))
        sheet_to_df_map1 = pd.ExcelFile(OutputFile)
        numberOfSheets=len(sheet_to_df_map1.sheet_names)
        x=range(0,numberOfSheets,1)
        wb = open_workbook(OutputFile)
        arr=[]
        for n in x:
            sheet = wb.sheet_by_index(n)
            if sheet.nrows >1:
                arr.append(html.Div(dt.DataTable(rows=sheet_to_df_map1.parse(n).to_dict('records'))))
            else :
                arr.append(html.P('No record Found',style={'color': 'red', 'fontSize': 24}))        

        #for n in x:
            #html.Div(html.H2('Latest Release Details')),html.Div(dt.DataTable(rows=sheet_to_df_map1.parse(1).to_dict('records')))
            #sheet=dt.DataTable(rows=sheet_to_df_map1.parse(n).to_dict('records'))
            #print (sheet)
        Title='TB- NonAppend Result List'
        #return html.Div(children=[html.H1(children=Title),html.Div(html.H2('Latest Order Details')),html.Div(dt.DataTable(rows=sheet_to_df_map1.parse(2).to_dict('records'))),html.Div(html.H2('Unsettled Invoice')),html.Div(dt.DataTable(rows=sheet_to_df_map1.parse(3).to_dict('records'))),html.Div(html.H2('Invoice Created')),html.Div(dt.DataTable(rows=sheet_to_df_map1.parse(4).to_dict('records')))])
        return html.Div(children=[html.H1(children=Title),html.Div(html.H2('Latest Release Details')),arr[0],html.Div(html.H2('Latest Order Details')),arr[2],html.Div(html.H2('Unsettled Invoice')),arr[3],html.Div(html.H2('Invoice Created')),arr[4],html.Div(html.H2('Agents Running List')),arr[5]])
if __name__ == '__main__':
    app.run_server(debug=True,host='9.202.181.216')