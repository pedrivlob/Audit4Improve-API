package us.muit.fs.a4i.test.control;

import java.util.Date;
import java.util.logging.Logger;


import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


import us.muit.fs.a4i.control.IndicatorsCalculator;
import us.muit.fs.a4i.exceptions.IndicatorException;
import us.muit.fs.a4i.model.entities.Indicator;
import us.muit.fs.a4i.model.entities.Metric;
import us.muit.fs.a4i.model.entities.Report;
import us.muit.fs.a4i.control.ReportManagerI;
import us.muit.fs.a4i.model.entities.ReportI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;


/**
 * <p>Test para probar la clase Report</p>
 * @author Isabel Rom�n
 *
 */
@ExtendWith(MockitoExtension.class)
class ReportManagerTest {
	private static Logger log=Logger.getLogger(ReportManagerTest.class.getName());
	/**
	 * <p>Objetos tipo Mock, sustitutos de las clases de las que depende Report</p>
	 * 
	 */
	@Mock(serializable =true)
	private static IndicatorsCalculator indCalcMock= Mockito.mock(IndicatorsCalculator.class);
		
	
	//Servir�n para conocer el argumento con el que se ha invocado alg�n m�todo de alguno de los mocks (sustitutos o representantes)
    //ArgumentCaptor es un gen�rico, indico al declararlo el tipo del argumento que quiero capturar
	@Captor
	private ArgumentCaptor<Integer> intCaptor;
	@Captor
	private ArgumentCaptor<String> strCaptor;
	@Captor
	private ArgumentCaptor<Metric> metricCaptor;
	@Captor
	private ArgumentCaptor<Indicator> indicatorCaptor;
	@Captor
	private ArgumentCaptor<Report> reportCaptor;
	
	@Mock(serializable = true)
	private static Metric<Integer> metricIntMock= Mockito.mock(Metric.class);
	@Mock(serializable = true)
	private static Metric<Date> metricDatMock= Mockito.mock(Metric.class);
	@Mock(serializable = true)
	private static Indicator<Integer> indicatorIntMock= Mockito.mock(Indicator.class);
	

	private static Report reportTested;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}


	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#addMetric(us.muit.fs.a4i.model.entities.Metric)}.
	 */
	@Test
	void testAddMetric() {
		reportTested=new Report();	
				
		//Primero se prueba a a�adir una m�trica de tipo Integer
		reportTested.addMetric(metricIntMock);
		//Verifico que se ha consultado el nombre una vez al invocar este m�todo, se usa como clave para meterlo en un mapa, hay que consultarlo
		//�Por qu� falla? �Con qu� no hab�a contado? �Hay problemas en el test o en el c�digo?
		//Prueba a sustituir por la l�nea comentada
		Mockito.verify(metricIntMock).getName();
		//Mockito.verify(metricIntMock, atLeast(1)).getName();
		Metric metric=reportTested.getMetricByName("issues");
		assertEquals(metric.getValue(),3,"Deber�a tener el valor especificado en el mock");
		assertEquals(metric.getDescription(),"Tareas sin finalizar en el repositorio","Deber�a tener el valor especificado en el mock");
		
		//Ahora se prueba una m�trica de tipo Date
		reportTested.addMetric(metricDatMock);
		metric=reportTested.getMetricByName("lastPush");
		assertEquals(metric.getValue(),metricDatMock.getValue(),"Deber�a tener el valor especificado en el mock");
		assertEquals(metric.getDescription(),"�ltimo push realizado en el repositorio","Deber�a tener el valor especificado en el mock");
		
		//Ahora se prueba a a�adir otra vez la misma m�trica pero con otro valor		
		reportTested.addMetric(metricIntMock);
		Mockito.when(metricIntMock.getValue()).thenReturn(55);	
		metric=reportTested.getMetricByName("issues");
		assertEquals(metric.getValue(),55,"Deber�a tener el valor especificado en el mock");
		assertEquals(metric.getDescription(),"Tareas sin finalizar en el repositorio","Deber�a tener el valor especificado en el mock");
	}


	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#calcIndicator(java.lang.String)}.
	 */
	@Test
	void testCalcIndicator() {
		//Definimos calculadora de tipo repositorio
		Mockito.when(indCalcMock.getReportType()).thenReturn(Report.Type.REPOSITORY);
		
		
		//Se configura la calculadora de indicadores del informe
		try {
			reportTested.setIndicatorsCalculator(indCalcMock);
			Mockito.verify(indCalcMock).getReportType();
		} catch (IndicatorException e1) {
			fail("No deber�a lanzar la excepci�n");
		}
		//Se solicita el c�lculo de un indicador determinado
		reportTested.calcIndicator("issues");
		//Se observa con qu� par�metros se invoca a la calculadora de indicadores
		try {
			Mockito.verify(indCalcMock).calcIndicator(strCaptor.capture(), reportCaptor.capture());
			//Elimine el comentario que aparece a continuaci�n, ejecute el test y explique por qu� falla
			//Mockito.verify(indCalcMock).calcAllIndicators(reportTested);
		} catch (IndicatorException e) {
			fail("No deber�a lanzar la excepci�n");
		}
		
		//Se verfica que se usa el nombre correcto y se pasa la referencia al informe correcto
		assertEquals("issues",strCaptor.getValue(),"Se solicita el c�lculo de la m�trica adecuada");
		assertEquals(reportTested,reportCaptor.getValue(),"Se pasa la referencia correcta del informe");
		//Hago un test que asegure que el propio informe captura y gestiona la excepci�n de que el indicador no existe
		try {
			Mockito.doThrow(new IndicatorException("El indicador no existe")).when(indCalcMock).calcIndicator("indKO", reportTested);
			reportTested.calcIndicator("indKO");			
		
		} catch (IndicatorException e) {
			fail("La excepci�n la debe gestionar el propio informe");
		}    
		
	}


}
