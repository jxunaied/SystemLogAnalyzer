import React, {Component} from 'react'
import {Chart} from 'react-google-charts'
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props) {
    super(props)
    this.state = {status: "", size: "", logData: "", histogramData: ""}
  }

  componentDidMount() {
    this.fetchStatus()
    this.fetchSize()
  }

  //Get Status
  fetchStatus = () => {
    fetch('http://localhost:8080/api/get_status',{
      mode: 'no-cors',
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then((res) => {
      res.json()
    }).then((res) =>{
      console.log(res)
      this.setState({ status: res.status }) 
    });
  }

  //Get File Size
  fetchSize = () => {
    fetch('http://localhost:8080/api/get_size',{
      mode: 'no-cors',
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then((res) => {
      res.json()
    }).then((res) =>{
      this.setState({ size: res.size }) 
    });
  }

  //Post dateTime To Get Log Data
  fetchLogData = (event) => {

    event.preventDefault();
    const data = new FormData(event.target);

    fetch('http://localhost:8080/api/data',{
      mode: 'no-cors',
      method: "POST",
      body:data,
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then((res) => {
      res.json()
    }).then((res) =>{
      this.setState({ logData: res }) 
    });
  }

  //Post dateTime To Get Histogram Data
  fetchHistogram = (event) => {

    event.preventDefault();
    const data = new FormData(event.target);

    fetch('http://localhost:8080/api/histogram',{
      mode: 'no-cors',
      method: "POST",
      body:data,
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then((res) => {
      res.json()
    }).then((res) =>{
      this.setState({ histogramData: res }) 
    });
  }


  render() {
    return (
      <div class="container">
        <h3 >1. System Log Analytics</h3>
        <br></br>
          <div class="container">
            <button onClick={this.fetchStatus}>Check Status</button>
            <label>Status:  {this.status}</label>  
            {/* <label>Status: {{ status }} </label> */}
            <button onClick={this.fetchSize}>Check Filesize</button>
            <label>Log File Size: {this.size} </label>
            {/* <label>Log File Size: {{ size }} </label> */}
          </div>
          <br></br>

          {/* Section Start for Log Data */}
          <div>
            <form onSubmit={this.fetchLogData}>
              <label>From: </label>
              <input type="date" name="datetimeFrom"></input>
              <label> Until: </label>
              <input type="date" name="datetimeUntil"></input>
              <label> Phrase: </label>
              <input type="text" name="phrase" placeholder="Search Phrase" id="submit"></input>
              <input type="submit" ></input>
            </form>
          </div>
          <br></br>
          <div>
            <table >
              <tr>
                <th>Date</th>
                <th>Message</th>
              </tr>
            
              {this.state.logData.map(( listValue, index ) => {
                  return (
                    <tr key={index}>
                      <td>{listValue.value.date}</td>
                      <td>{listValue.value.message}</td>
                    </tr>
                  );
              })}
              
            </table>
          </div>
          <br/>
          {/* Section End for Log  Data */}

          {/* Section Start for histogram */}
          <div>
            <h3>2. System Log Histogram</h3>
            <br></br>
            <div>
              <form onSubmit={this.fetchHistogram}>
                <label>From: </label>
                <input type="date" name="datetimeFrom"></input>
                <label> Until: </label>
                <input type="date" name="datetimeUntil"></input>
                <label> Phrase: </label>
                <input type="text" name="phrase" placeholder="Search Phrase" id="submit"></input>
                <input type="submit" ></input>
              </form>
            </div>
            {/* //Added demo data for histogram*/}
            <Chart
                width={'500px'}
                height={'300px'}
                chartType="Histogram"
                loader={<div>Loading Chart</div>}
                data={histogramData.histogram}
                options={{
                  title: 'Date VS Count',
                  legend: { position: 'none' },
                  colors: ['#e7711c'],
                  histogram: { lastBucketPercentile: 5 },
                  vAxis: { scaleType: 'mirrorLog' },
                }}
                rootProps={{ 'data-testid': '3' }}
              />
          </div>
          {/* Section End for Histogram */}
      </div>
    );
  }
}

export default App;
