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
      crossDomain:true,
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then((res) => {
      res.json()
    }).then((res) =>{
      this.setState({ status: res.status }) 
    });
  }

  //Get File Size
  fetchSize = () => {
    fetch('http://localhost:8080/api/get_size',{
      crossDomain:true,
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
      crossDomain:true,
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
      crossDomain:true,
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
            <label>Status: Ok </label>  
            {/* <label>Status: {{ status }} </label> */}
            <button onClick={this.fetchSize}>Check Filesize</button>
            <label>Log File Size: Ok </label>
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
                <th>Month</th>
                <th>Date</th>
                <th>Time</th>
                <th>Server Name</th>
                <th>PID</th>
                <th>Log</th>
              </tr>
            
              {/* {this.state.logData.map(( listValue, index ) => {
                  return (
                    <tr key={index}>
                      <td>{{listValue.}}</td>
                      <td>{{listValue.}}</td>
                      <td>{{listValue.}}</td>
                      <td>{{listValue.}}</td>
                      <td>{{listValue.}}</td>
                      <td>{{listValue.}}</td>
                    </tr>
                  );
              })} */}
              
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
                data={[
                  ['Dinosaur', 'Length'],
                  ['Acrocanthosaurus (top-spined lizard)', 12.2],
                  ['Albertosaurus (Alberta lizard)', 9.1],
                  ['Allosaurus (other lizard)', 12.2],
                  ['Apatosaurus (deceptive lizard)', 22.9],
                  ['Archaeopteryx (ancient wing)', 0.9],
                  ['Argentinosaurus (Argentina lizard)', 36.6],
                  ['Baryonyx (heavy claws)', 9.1],
                  ['Brachiosaurus (arm lizard)', 30.5],
                  ['Ceratosaurus (horned lizard)', 6.1],
                  ['Coelophysis (hollow form)', 2.7],
                  ['Compsognathus (elegant jaw)', 0.9],
                  ['Deinonychus (terrible claw)', 2.7],
                  ['Diplodocus (double beam)', 27.1],
                  ['Dromicelomimus (emu mimic)', 3.4],
                  ['Gallimimus (fowl mimic)', 5.5],
                  ['Mamenchisaurus (Mamenchi lizard)', 21.0],
                  ['Megalosaurus (big lizard)', 7.9],
                  ['Microvenator (small hunter)', 1.2],
                  ['Ornithomimus (bird mimic)', 4.6],
                  ['Oviraptor (egg robber)', 1.5],
                  ['Plateosaurus (flat lizard)', 7.9],
                  ['Sauronithoides (narrow-clawed lizard)', 2.0],
                  ['Seismosaurus (tremor lizard)', 45.7],
                  ['Spinosaurus (spiny lizard)', 12.2],
                  ['Supersaurus (super lizard)', 30.5],
                  ['Tyrannosaurus (tyrant lizard)', 15.2],
                  ['Ultrasaurus (ultra lizard)', 30.5],
                  ['Velociraptor (swift robber)', 1.8],
                ]}
                options={{
                  title: 'Time VS Log',
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
