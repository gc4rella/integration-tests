package org.project.neutrino.nfvo.core.test;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.project.neutrino.nfvo.catalogue.mano.common.DeploymentFlavour;
import org.project.neutrino.nfvo.catalogue.mano.common.HighAvailability;
import org.project.neutrino.nfvo.catalogue.mano.common.VNFDeploymentFlavour;
import org.project.neutrino.nfvo.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.project.neutrino.nfvo.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.project.neutrino.nfvo.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.project.neutrino.nfvo.catalogue.nfvo.NFVImage;
import org.project.neutrino.nfvo.catalogue.nfvo.Network;
import org.project.neutrino.nfvo.catalogue.nfvo.Subnet;
import org.project.neutrino.nfvo.catalogue.nfvo.VimInstance;
import org.project.neutrino.nfvo.core.interfaces.NetworkManagement;
import org.project.neutrino.nfvo.repositories_interfaces.GenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by lto on 20/04/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(classes = { ApplicationTest.class })
@TestPropertySource(properties = { "timezone = GMT", "port: 4242" })
public class NetworkManagementClassSuiteTest {

	private Logger log = LoggerFactory.getLogger(ApplicationTest.class);

	@Rule
	public ExpectedException exception = ExpectedException.none();


	@Autowired
	private NetworkManagement networkManagement;

	@Autowired
	@Qualifier("networkRepository")
	private GenericRepository<Network> networkRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(ApplicationTest.class);
		log.info("Starting test");
	}

	@Test
	public void nfvImageManagementNotNull(){
		Assert.assertNotNull(networkManagement);
	}

	@Test
	public void networkManagementUpdateTest(){
		Network network_exp = createNetwork();
		when(networkRepository.find(anyString())).thenReturn(network_exp);

		Network network_new = createNetwork();
		network_new.setName("UpdatedName");
		network_new.setExternal(true);
		network_exp = networkManagement.update(network_new, network_exp.getId());

		Assert.assertEquals(network_exp.getName(), network_new.getName());
		Assert.assertEquals(network_exp.getExtId(), network_new.getExtId());
		Assert.assertEquals(network_exp.getExternal(), network_new.getExternal());
	}

	private Network createNetwork() {
		Network network = new Network();
		network.setName("network_name");
		network.setExtId("ext_id");
		network.setExternal(false);
		network.setNetworkType("network_type");
		network.setPhysicalNetworkName("physical_network_name");
		network.setSegmentationId(0);
		network.setShared(false);
		network.setSubnets(new ArrayList<Subnet>() {{
			add(createSubnet());
		}});
		return network;
	}

	private Subnet createSubnet() {
		final Subnet subnet = new Subnet();
		subnet.setName("subnet_name");
		subnet.setExtId("ext_id");
		subnet.setCidr("cidr");
		subnet.setNetworkId("network_id");
		return subnet;
	}

	@Test
	public void networkManagementAddTest(){
		Network network_exp = createNetwork();
		when(networkRepository.create(any(Network.class))).thenReturn(network_exp);
		Network network_new = networkManagement.add(network_exp);

		Assert.assertEquals(network_exp.getId(), network_new.getId());
		Assert.assertEquals(network_exp.getName(), network_new.getName());
		Assert.assertEquals(network_exp.getExtId(), network_new.getExtId());
		Assert.assertEquals(network_exp.getExternal(), network_new.getExternal());
		Assert.assertEquals(network_exp.getSegmentationId(), network_new.getSegmentationId());
	}

	@Test
	public void networkManagementQueryTest(){
		when(networkRepository.findAll()).thenReturn(new ArrayList<Network>());

		Assert.assertEquals(0, networkManagement.query().size());

		Network network_exp = createNetwork();
		when(networkRepository.find(network_exp.getId())).thenReturn(network_exp);
		Network network_new = networkManagement.query(network_exp.getId());
		Assert.assertEquals(network_exp.getId(), network_new.getId());
		Assert.assertEquals(network_exp.getName(), network_new.getName());
		Assert.assertEquals(network_exp.getExtId(), network_new.getExtId());
		Assert.assertEquals(network_exp.getExternal(), network_new.getExternal());
		Assert.assertEquals(network_exp.getSegmentationId(), network_new.getSegmentationId());
	}

	@Test
	public void networkManagementDeleteTest(){
		Network network_exp = createNetwork();
		when(networkRepository.find(network_exp.getId())).thenReturn(network_exp);
		networkManagement.delete(network_exp.getId());
		when(networkRepository.find(network_exp.getId())).thenReturn(null);
		Network network_new = networkManagement.query(network_exp.getId());
		Assert.assertNull(network_new);
	}

	@AfterClass
	public static void shutdown() {
		// TODO Teardown to avoid exceptions during test shutdown
	}


	private NFVImage createNfvImage() {
		NFVImage nfvImage = new NFVImage();
		nfvImage.setName("image_name");
		nfvImage.setExtId("ext_id");
		nfvImage.setMinCPU("1");
		nfvImage.setMinRam(1024);
		return nfvImage;
	}

	private NetworkServiceDescriptor createNetworkServiceDescriptor() {
		final NetworkServiceDescriptor nsd = new NetworkServiceDescriptor();
		nsd.setVendor("FOKUS");
		Set<VirtualNetworkFunctionDescriptor> virtualNetworkFunctionDescriptors = new HashSet<VirtualNetworkFunctionDescriptor>();
		VirtualNetworkFunctionDescriptor virtualNetworkFunctionDescriptor = new VirtualNetworkFunctionDescriptor();
		virtualNetworkFunctionDescriptor
				.setMonitoring_parameter(new HashSet<String>() {
					{
						add("monitor1");
						add("monitor2");
						add("monitor3");
					}
				});
		virtualNetworkFunctionDescriptor.setDeployment_flavour(new HashSet<VNFDeploymentFlavour>() {{
			VNFDeploymentFlavour vdf = new VNFDeploymentFlavour();
			vdf.setExtId("ext_id");
			vdf.setFlavour_key("flavor_name");
			add(vdf);
		}});
		virtualNetworkFunctionDescriptor
				.setVdu(new HashSet<VirtualDeploymentUnit>() {
					{
						VirtualDeploymentUnit vdu = new VirtualDeploymentUnit();
						vdu.setHigh_availability(HighAvailability.ACTIVE_ACTIVE);
						vdu.setComputation_requirement("high_requirements");
						VimInstance vimInstance = new VimInstance();
						vimInstance.setName("vim_instance");
						vimInstance.setType("test");
						vdu.setVimInstance(vimInstance);
						add(vdu);
					}
				});
		virtualNetworkFunctionDescriptors.add(virtualNetworkFunctionDescriptor);
		nsd.setVnfd(virtualNetworkFunctionDescriptors);
		return nsd;
	}

	private VimInstance createVimInstance() {
		VimInstance vimInstance = new VimInstance();
		vimInstance.setName("vim_instance");
		vimInstance.setType("test");
		vimInstance.setNetworks(new ArrayList<Network>() {{
			Network network = new Network();
			network.setExtId("ext_id");
			network.setName("network_name");
			add(network);
		}});
		vimInstance.setFlavours(new ArrayList<DeploymentFlavour>() {{
			DeploymentFlavour deploymentFlavour = new DeploymentFlavour();
			deploymentFlavour.setExtId("ext_id_1");
			deploymentFlavour.setFlavour_key("flavor_name");
			add(deploymentFlavour);

			deploymentFlavour = new DeploymentFlavour();
			deploymentFlavour.setExtId("ext_id_2");
			deploymentFlavour.setFlavour_key("m1.tiny");
			add(deploymentFlavour);
		}});
		vimInstance.setImages(new ArrayList<NFVImage>() {{
			NFVImage image = new NFVImage();
			image.setExtId("ext_id_1");
			image.setName("ubuntu-14.04-server-cloudimg-amd64-disk1");
			add(image);

			image = new NFVImage();
			image.setExtId("ext_id_2");
			image.setName("image_name_1");
			add(image);
		}});
		return vimInstance;
	}

}
